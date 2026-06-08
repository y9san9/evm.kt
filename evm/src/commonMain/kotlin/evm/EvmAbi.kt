/*
 Based on https://docs.soliditylang.org/en/latest/abi-spec.html
 */
package evm

public object EvmAbi {
    public const val ADDRESS_SIZE_BYTES: Int = 20
    public const val SIGNATURE_SIZE_BYTES: Int = 4
    public const val PARAMETER_SIZE_BYTES: Int = 32

    public fun encodeCallData(
        signature: Signature,
        parameters: List<Parameter>,
    ): EvmCallData {
        val bytes = encodeSignature(signature).unsafeBytes +
            parameters.fold(byteArrayOf()) { bytes, parameter ->
                bytes + encodeParameter(parameter).unsafeBytes
            }
        return EvmCallData(EvmHex.unsafe(bytes))
    }

    public fun encodeSignature(signature: Signature): EvmHex =
        when (signature) {
            is Signature.String -> {
                val hex = EvmHex.unsafe(signature.string.encodeToByteArray())
                val keccak = EvmKeccak256.digest(hex)
                val first = EvmHex(SIGNATURE_SIZE_BYTES) { i -> keccak[i] }
                first
            }
            is Signature.Hex -> signature.hex
        }

    public fun encodeParameter(parameter: Parameter): EvmHex =
        when (parameter) {
            is Parameter.Address -> encodeParameter(parameter)
        }

    public fun encodeParameter(parameter: Parameter.Address): EvmHex {
        val padSize = PARAMETER_SIZE_BYTES - ADDRESS_SIZE_BYTES
        return EvmHex(PARAMETER_SIZE_BYTES) { i ->
            if (i < padSize) {
                0
            } else {
                parameter.address.hex[i - padSize]
            }
        }
    }

    public fun <T> decodeCallResult(
        result: EvmCallResult,
        descriptor: Descriptor<T>,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return when (descriptor) {
            is Descriptor.UInt -> decodeCallResult(result, descriptor)
        } as T
    }

    public fun decodeCallResult(
        result: EvmCallResult,
        descriptor: Descriptor.UInt,
    ): EvmInteger {
        val integer = result.hex.toIntegerBigEndian()
        require(integer <= EvmInteger.maxValueUnsigned(descriptor.sizeBytes))
        return integer
    }

    public fun signature(string: String): Signature.String =
        Signature.String(string)

    public sealed interface Signature {
        public data class String(val string: kotlin.String) : Signature
        public data class Hex(val hex: EvmHex) : Signature
    }

    public sealed interface Parameter {
        public data class Address(val address: EvmAddress) : Parameter
    }

    public sealed interface Descriptor<out T> {
        public data class UInt(val sizeBytes: Int) : Descriptor<EvmInteger>

        public companion object {
            public val UInt256: UInt get() = UInt(sizeBytes = 32)
        }
    }
}
