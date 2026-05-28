package evm

import kotlinx.serialization.Serializable

public data class EvmCall(
    val from: EvmAddress? = null,
    val to: EvmAddress? = null,
    val gas: EvmInteger? = null,
    val gasPrice: EvmInteger? = null,
    val maxPriorityFeeGas: EvmInteger? = null,
    val maxFeePerGas: EvmInteger? = null,
    val value: EvmInteger? = null,
    val data: EvmHex? = null,
) {
    public fun serializable(): EvmCallSerializable = EvmCallSerializable(
        from = from?.serializable(),
        to = to?.serializable(),
        gas = gas?.serializable(),
        gasPrice = gasPrice?.serializable(),
        maxPriorityFeeGas = maxPriorityFeeGas?.serializable(),
        maxFeePerGas = maxFeePerGas?.serializable(),
        value = value?.serializable(),
        data = data?.serializable(),
    )
}

@Serializable
public data class EvmCallSerializable(
    val from: EvmAddressSerializable? = null,
    val to: EvmAddressSerializable? = null,
    val gas: EvmIntegerSerializable? = null,
    val gasPrice: EvmIntegerSerializable? = null,
    val maxPriorityFeeGas: EvmIntegerSerializable? = null,
    val maxFeePerGas: EvmIntegerSerializable? = null,
    val value: EvmIntegerSerializable? = null,
    val data: EvmHexSerializable? = null,
) {
    public fun typed(): EvmCall = EvmCall(
        from = from?.typed(),
        to = to?.typed(),
        gas = gas?.typed(),
        gasPrice = gasPrice?.typed(),
        maxPriorityFeeGas = maxPriorityFeeGas?.typed(),
        maxFeePerGas = maxFeePerGas?.typed(),
        value = value?.typed(),
        data = data?.typed(),
    )
}
