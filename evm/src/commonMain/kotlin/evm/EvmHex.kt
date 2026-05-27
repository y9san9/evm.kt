package evm

import kotlinx.serialization.Serializable

/**
 * [EvmHex] is a ByteArray wrapper with various integrations and helpers.
 */
public data class EvmHex(public val unsafeBytes: ByteArray) {
    public val stringWithoutPrefix: String get() = unsafeBytes.toHexString()
    public val stringWithPrefix: String get() = "0x$stringWithoutPrefix"
    public val size: Int get() = unsafeBytes.size

    public fun copyOfBytes(): ByteArray = unsafeBytes.copyOf()

    public fun serializable(): EvmHexSerializable {
        return EvmHexSerializable(stringWithPrefix)
    }

    public fun number(): EvmNumber {
        return EvmNumber(hex = this)
    }

    public fun toMutableHex(): EvmMutableHex =
        EvmMutableHex(unsafeBytes.toMutableList())

    override fun toString(): String = stringWithPrefix

    public companion object {
        public val Alphabet: CharArray = charArrayOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f',
        )

        public fun orThrow(string: String): EvmHex {
            require(string.startsWith("0x"))
            val stringWithoutPrefix = string.drop(2)
            require(
                stringWithoutPrefix.lowercase().all { char ->
                    char in Alphabet
                },
            )
            return EvmHex(stringWithoutPrefix.hexToByteArray())
        }

        public fun copyFrom(bytes: ByteArray): EvmHex {
            return EvmHex(bytes.copyOf())
        }
    }
}

public data class EvmMutableHex(
    private val unsafeBytes: MutableList<Byte>,
) {
    public constructor() : this(mutableListOf())

    public fun add(hex: EvmHex) {
        unsafeBytes += hex.unsafeBytes.asList()
    }

    public fun add(vararg byte: Byte) {
        unsafeBytes += byte.asList()
    }

    public fun toHex(): EvmHex = EvmHex(unsafeBytes.toByteArray())
}

public inline fun buildEvmHex(
    hex: EvmHex? = null,
    block: EvmMutableHex.() -> Unit,
): EvmHex {
    val mutable = hex?.toMutableHex() ?: EvmMutableHex()
    mutable.block()
    return mutable.toHex()
}

@Serializable
@JvmInline
public value class EvmHexSerializable(public val string: String) {
    init {
        EvmHex.orThrow(string)
    }

    public fun typed(): EvmHex = EvmHex.orThrow(string)
}
