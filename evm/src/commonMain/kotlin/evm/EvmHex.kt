package evm

import kotlinx.serialization.Serializable

/**
 * This class has immutability contract. Bytes should not be mutated. Use
 * [EvmHex].
 */
public class EvmHex private constructor(public val unsafeBytes: ByteArray) {
    public fun serializable(): EvmHexSerializable {
        val string = "0x${unsafeBytes.toHexString()}"
        return EvmHexSerializable(string)
    }

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
    }
}

@Serializable
@JvmInline
public value class EvmHexSerializable(public val string: String) {
    public fun typed(): EvmHex = EvmHex.orThrow(string)
}
