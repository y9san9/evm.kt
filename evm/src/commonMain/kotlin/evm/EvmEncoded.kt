package evm

import kotlinx.serialization.Serializable

public data class EvmEncoded(val number: EvmNumber) {
    public fun serializable(): EvmEncodedSerializable =
        EvmEncodedSerializable(string = number.toHexString())
}

@Serializable
@JvmInline
public value class EvmEncodedSerializable(public val string: String) {
    public fun typed(): EvmEncoded =
        EvmEncoded(number = EvmNumber.fromHexOrThrow(string))
}
