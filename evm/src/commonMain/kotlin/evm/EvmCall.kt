package evm

import kotlinx.serialization.Serializable

public data class EvmCall(
    val from: EvmAddress? = null,
    val to: EvmAddress? = null,
    val gas: EvmNumber? = null,
    val gasPrice: EvmNumber? = null,
    val maxPriorityFeeGas: EvmNumber? = null,
    val maxFeePerGas: EvmNumber? = null,
    val value: EvmNumber? = null,
    val data: EvmEncoded? = null,
) {
    public fun serializable(): EvmCallSerializable {
        return EvmCallSerializable(
            from = from?.serializable(),
            to = to?.serializable(),
            gas = gas?.serializable(),
            gasPrice = gasPrice?.hexSerializable(),
            maxPriorityFeeGas = maxPriorityFeeGas?.hexSerializable(),
            maxFeePerGas = maxFeePerGas?.hexSerializable(),
            value = value?.hexSerializable(),
            data = data?.serializable(),
        )
    }
}

@Serializable
public data class EvmCallSerializable(
    val from: EvmAddressSerializable? = null,
    val to: EvmAddressSerializable? = null,
    val gas: EvmNumberSerializable? = null,
    val gasPrice: EvmHexSerializable? = null,
    val maxPriorityFeeGas: EvmHexSerializable? = null,
    val maxFeePerGas: EvmHexSerializable? = null,
    val value: EvmHexSerializable? = null,
    val data: EvmEncodedSerializable? = null,
) {
    public fun typed(): EvmCall {
        return EvmCall(
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
}
