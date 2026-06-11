package evm

import kotlinx.serialization.Serializable

public inline fun EvmHex(size: Int, init: (index: Int) -> Byte): EvmHex =
    EvmHex.unsafe(
        ByteArray(size) { i ->
            init(i)
        },
    )

/**
 * This class has immutability contract. Bytes should not be mutated.
 *
 * TODO: Add `from: Int, to: Int`, so it is possible to make slices
 */
public class EvmHex private constructor(public val unsafeBytes: ByteArray) {
    public val size: Int get() = unsafeBytes.size

    public fun serializable(): EvmHexSerializable {
        val string = "0x${unsafeBytes.toHexString()}"
        return EvmHexSerializable(string)
    }

    public fun toIntegerBigEndian(): EvmInteger =
        EvmInteger.fromHexUnsignedBigEndian(hex = this)

    public operator fun get(index: Int): Byte = unsafeBytes[index]

    public fun copyOfBytes(): ByteArray = unsafeBytes.copyOf()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is EvmHex) return false
        return this.unsafeBytes.contentEquals(other.unsafeBytes)
    }

    override fun hashCode(): Int = unsafeBytes.contentHashCode()

    override fun toString(): String = serializable().string

    public companion object {
        /**
         * [bytes] ownership is transferred to [EvmMemory]. Caller is
         * responsible for that.
         */
        public fun unsafe(bytes: ByteArray): EvmHex = EvmHex(bytes)

        public fun orThrow(string: String): EvmHex {
            require(string.startsWith("0x"))
            val bytes = string.drop(2).hexToByteArray()
            return unsafe(bytes)
        }

        public fun encode(string: String): EvmHex =
            EvmHex(string.encodeToByteArray())
    }
}

@Serializable
@JvmInline
public value class EvmHexSerializable(public val string: String) {
    public fun typed(): EvmHex = EvmHex.orThrow(string)
}
