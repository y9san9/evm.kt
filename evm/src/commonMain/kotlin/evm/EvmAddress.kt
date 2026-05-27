package evm

import kotlinx.serialization.Serializable

/**
 * Wallet or smart-contract address
 */
public data class EvmAddress(val hex: EvmHex) {
    public fun serializable(): EvmAddressSerializable {
        return EvmAddressSerializable(hex.serializable())
    }
}

@Serializable
@JvmInline
public value class EvmAddressSerializable(public val hex: EvmHexSerializable) {
    public fun typed(): EvmAddress {
        return EvmAddress(hex.typed())
    }
}
