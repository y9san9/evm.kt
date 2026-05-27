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
    val data: EvmHex? = null,
) {
    public fun serializable(): EvmCallSerializable {
        return EvmCallSerializable(
            from = from?.serializable(),
            to = to?.serializable(),
            gas = gas?.serializable(),
            gasPrice = gasPrice?.hex()?.serializable(),
            maxPriorityFeeGas = maxPriorityFeeGas?.hex()?.serializable(),
            maxFeePerGas = maxFeePerGas?.hex()?.serializable(),
            value = value?.hex()?.serializable(),
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
    val data: EvmHexSerializable? = null,
) {
    public fun typed(): EvmCall {
        return EvmCall(
            from = from?.typed(),
            to = to?.typed(),
            gas = gas?.typed(),
            gasPrice = gasPrice?.typed()?.number(),
            maxPriorityFeeGas = maxPriorityFeeGas?.typed()?.number(),
            maxFeePerGas = maxFeePerGas?.typed()?.number(),
            value = value?.typed()?.number(),
            data = data?.typed(),
        )
    }
}
