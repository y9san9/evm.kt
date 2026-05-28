package evm

import kotlinx.serialization.Serializable

public data class EvmSignature(val hex: EvmHex) {
    public fun serializable(): EvmSignatureSerializable =
        EvmSignatureSerializable(hex = hex.serializable())
}

@Serializable
@JvmInline
public value class EvmSignatureSerializable(
    public val hex: EvmHexSerializable,
) {
    public fun typed(): EvmSignature = EvmSignature(hex = hex.typed())
}
