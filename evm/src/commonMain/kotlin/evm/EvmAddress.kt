package evm

import kotlinx.serialization.Serializable

/**
 * Wallet or smart-contract address
 */
public data class EvmAddress(val hex: EvmHex) {
    public fun serializable(): EvmAddressSerializable =
        EvmAddressSerializable(hex.serializable())

    public companion object {
        public fun orThrow(string: String): EvmAddress =
            EvmAddress(EvmHex.orThrow(string))
    }
}

@Serializable
@JvmInline
public value class EvmAddressSerializable(public val hex: EvmHexSerializable) {
    public fun typed(): EvmAddress = EvmAddress(hex.typed())
}
