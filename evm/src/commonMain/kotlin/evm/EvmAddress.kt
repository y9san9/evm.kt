package evm

import kotlinx.serialization.Serializable

/**
 * Wallet or smart-contract address
 */
public data class EvmAddress(val number: EvmNumber) {
    public fun serializable(): EvmAddressSerializable {
        return EvmAddressSerializable(number.toHexString())
    }
}

@Serializable
@JvmInline
public value class EvmAddressSerializable(public val string: String) {
    public fun typed(): EvmAddress {
        return EvmAddress(
            number = EvmNumber.fromHexOrThrow(string),
        )
    }
}
