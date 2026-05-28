package evm

/**
 * Keccak-256: the hash function used throughout the Ethereum Virtual Machine.
 *
 * NOTE: This is Keccak-256, not SHA-3-256. They differ only in the padding byte:
 *       Ethereum uses 0x01, the SHA-3 standard uses 0x06.
 *
 * THE STATE is a 5x5 matrix of 64-bit integers.
 *
 * How it works, the "sponge" construction:
 *   1. Absorb: XOR input bytes into a 200-byte state, one chunk at a time.
 *   2. Pad: Mark the end of the input with a specific bit pattern.
 *   3. Squeeze: Read the first 32 bytes of the final state as the digest.
 *
 * After each chunk is absorbed, THE STATE is scrambled by keccakf(),
 * which runs 24 rounds of four mixing steps: Theta, Rho+Pi, Chi, Iota.
 *
 * Spec: https://keccak.team/keccak_specs_summary.html
 * Reference implementation: https://github.com/mjosaarinen/tiny_sha3/blob/master/sha3.c
 */
public object EvmKeccak256 {
    // Keccak-256 always produces a 32-byte (256-bit) digest.
    private const val DIGEST_BYTES = 32

    // THE STATE is a 5x5 matrix of 64-bit lanes = 25 lanes × 8 bytes = 200 bytes.
    private const val STATE_BYTES = 200

    // The "rate" is how many bytes of THE STATE are XORed by input per chunk.
    // THE STATE is only touched by input during the absorbtion phase.
    // The remaining bytes (the "capacity") are never directly touched by input,
    // which is what gives the sponge its security. For a 256-bit digest:
    // rate = 200 - 2 x 32 = 136 bytes  (exposed)
    // capacity = 2 x 32 =  64 bytes  (hidden)
    private const val RATE_BYTES = STATE_BYTES - 2 * DIGEST_BYTES // = 136

    // After the last input byte, two markers are XORed in to close the block:
    // Keccak Domain Separator: [last input byte position] ^= 0x01
    // End of padding: [last byte of rate block] ^= 0x80
    //
    // 0x01 is Keccak's domain separator. SHA-3 uses 0x06, SHAKE uses 0x1F.
    // Ethereum follows the original Keccak submission, not the NIST SHA-3 standard.
    private const val KECCAK_DOMAIN_SEPARATOR = 0x01
    private const val END_OF_PADDING = 0x80

    // keccakf() runs exactly 24 rounds. This is fixed by the spec for the
    // 1600-bit state variant (Keccak-f[1600]).
    private const val ROUNDS = 24

    // Each round, a unique 64-bit constant is XORed into lane [0,0] (the Iota step).
    // This breaks round-symmetry and prevents slide attacks.
    // These 24 values are not arbitrary, changing any one of them produces a
    // non-standard hash.
    private val IOTA_ROUND_CONSTANTS = ulongArrayOf(
        0x0000000000000001UL, 0x0000000000008082UL, 0x800000000000808aUL,
        0x8000000080008000UL, 0x000000000000808bUL, 0x0000000080000001UL,
        0x8000000080008081UL, 0x8000000000008009UL, 0x000000000000008aUL,
        0x0000000000000088UL, 0x0000000080008009UL, 0x000000008000000aUL,
        0x000000008000808bUL, 0x800000000000008bUL, 0x8000000000008089UL,
        0x8000000000008003UL, 0x8000000000008002UL, 0x8000000000000080UL,
        0x000000000000800aUL, 0x800000008000000aUL, 0x8000000080008081UL,
        0x8000000000008080UL, 0x0000000080000001UL, 0x8000000080008008UL,
    )

    // The Rho step rotates each of the 24 non-zero lanes left by a fixed number of
    // bits. Lane [0,0] is never rotated. The amounts below correspond to lanes visited
    // in the order defined by PI_LANE_ORDER (Rho and Pi are applied in a single pass).
    private val RHO_ROTATION_AMOUNTS = intArrayOf(
        1, 3, 6, 10, 15, 21, 28, 36,
        45, 55, 2, 14, 27, 41, 56, 8,
        25, 43, 62, 18, 39, 61, 20, 44,
    )

    // The Pi step shuffles all 25 lanes to new positions in the matrix.
    // Lane [0,0] stays in place; the other 24 are cycled through the indices below.
    // Combined with Rho in one pass: rotate lane at current index, write to next index.
    private val PI_LANE_ORDER = intArrayOf(
        10, 7, 11, 17, 18, 3, 5, 16,
        8, 21, 24, 4, 15, 23, 19, 13,
        12, 2, 20, 14, 22, 9, 6, 1,
    )

    public fun digest(hex: EvmHex): EvmHex {
        val state = ByteArray(STATE_BYTES)
        val input = hex.unsafeBytes

        absorb(state, input)
        pad(state, endOfInputPosition = input.size % RATE_BYTES)
        keccakf(state)

        return EvmHex.unsafe(state.copyOf(DIGEST_BYTES))
    }

    // XOR each input byte into the state at the current position.
    // Once RATE_BYTES bytes have been absorbed, permute the state and start over.
    private fun absorb(state: ByteArray, input: ByteArray) {
        var position = 0
        for (byte in input) {
            state[position] =
                (state[position].toInt() xor byte.toInt()).toByte()
            position++
            if (position == RATE_BYTES) {
                keccakf(state)
                position = 0
            }
        }
    }

    // XOR two markers into the state to close the final, potentially partial block.
    // This unambiguously signals where the message ends inside the rate window.
    private fun pad(state: ByteArray, endOfInputPosition: Int) {
        state[endOfInputPosition] =
            (state[endOfInputPosition].toInt() xor KECCAK_DOMAIN_SEPARATOR).toByte()
        state[RATE_BYTES - 1] =
            (state[RATE_BYTES - 1].toInt() xor END_OF_PADDING).toByte()
    }

    // The state bytes are first unpacked into 25 little-endian 64-bit lanes,
    // scrambled by 24 rounds, then packed back into bytes.
    private fun keccakf(state: ByteArray) {
        val lanes = unpackToLanes(state)
        repeat(ROUNDS) { round -> applyRound(lanes, round) }
        packFromLanes(lanes, state)
    }

    private fun applyRound(lanes: LongArray, round: Int) {
        theta(lanes)
        rhoPi(lanes)
        chi(lanes)
        iota(lanes, round)
    }

    // THETA: column-parity mixing.
    //
    // Computes the XOR parity of each column, then mixes two neighbouring column
    // parities into every lane. A single changed bit fans out to affect all lanes,
    // providing rapid diffusion across the state.
    //
    // parity[x] = lane[x,0] ^ lane[x,1] ^ lane[x,2] ^ lane[x,3] ^ lane[x,4]
    // lane[x,y] ^= parity[x-1] ^ ROT(parity[x+1], 1)
    private fun theta(lanes: LongArray) {
        val columnParity = LongArray(5) { x ->
            lanes[x] xor lanes[x + 5] xor lanes[x + 10] xor lanes[x + 15] xor
                lanes[x + 20]
        }
        for (x in 0 until 5) {
            val neighborMix =
                columnParity[(x + 4) % 5] xor
                    rotateLeft64(columnParity[(x + 1) % 5], 1)
            for (y in 0 until 5) {
                lanes[x + y * 5] = lanes[x + y * 5] xor neighborMix
            }
        }
    }

    // RHO + PI: bit rotation and lane transposition, fused into one pass.
    //
    // Rho rotates each lane's bits left by a lane-specific amount (RHO_ROTATION_AMOUNTS).
    // Pi moves each lane to a new position in the matrix (PI_LANE_ORDER).
    //
    // Fusing them avoids an extra temporary array: we carry one lane at a time,
    // rotate it, drop it at its Pi-destination, and pick up what was there.
    private fun rhoPi(lanes: LongArray) {
        var carry = lanes[1]
        for (i in 0 until 24) {
            val destination = PI_LANE_ORDER[i]
            val displaced = lanes[destination]
            lanes[destination] = rotateLeft64(carry, RHO_ROTATION_AMOUNTS[i])
            carry = displaced
        }
    }

    // CHI non-linear row mixing.
    //
    // The only non-linear step; it is what makes the permutation one-way.
    // Each lane is XORed with a function of its two right-neighbours in the same row:
    //
    // lane[x,y] ^= (~lane[x+1,y]) & lane[x+2,y]
    private fun chi(lanes: LongArray) {
        for (y in 0 until 5) {
            val row = LongArray(5) { x -> lanes[x + y * 5] }
            for (x in 0 until 5) {
                lanes[x + y * 5] =
                    row[x] xor (row[(x + 1) % 5].inv() and row[(x + 2) % 5])
            }
        }
    }

    // IOTA: round differentiation.
    //
    // XORs a unique constant into lane [0,0] so that no two rounds are identical.
    // Without this, the permutation would be vulnerable to symmetry-based attacks.
    private fun iota(lanes: LongArray, round: Int) {
        lanes[0] = lanes[0] xor IOTA_ROUND_CONSTANTS[round].toLong()
    }

    // Keccak treats its state as little-endian 64-bit words.
    // Byte 0 of the state is the least-significant byte of lane 0.
    private fun unpackToLanes(state: ByteArray): LongArray =
        LongArray(25) { laneIndex ->
            val offset = laneIndex * 8
            var lane = 0L
            for (b in 0 until 8) {
                lane =
                    lane or ((state[offset + b].toLong() and 0xFF) shl (b * 8))
            }
            lane
        }

    private fun packFromLanes(lanes: LongArray, state: ByteArray) {
        for (laneIndex in 0 until 25) {
            val offset = laneIndex * 8
            val lane = lanes[laneIndex]
            for (b in 0 until 8) {
                state[offset + b] = ((lane ushr (b * 8)) and 0xFF).toByte()
            }
        }
    }

    private fun rotateLeft64(value: Long, amount: Int): Long =
        (value shl amount) or (value ushr (64 - amount))
}
