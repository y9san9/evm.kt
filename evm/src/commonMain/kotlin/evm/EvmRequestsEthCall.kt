package evm

public data class EvmTransactionCallObject(
    val from: EvmAddress,
    val to: EvmAddress?,
    val gas: EvmNumber?,
    val gasPrice: EvmNumber?,
    val maxPriorityFeeGas: EvmNumber?,
    val maxFeeGas: EvmNumber?,
    val value: EvmNumber,
    val data: EvmSignature,
)
