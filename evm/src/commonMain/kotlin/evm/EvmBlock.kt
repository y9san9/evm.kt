package evm

public sealed interface EvmBlock {
    public data object Latest : EvmBlock
    public data object Earlier : EvmBlock
    public data object Pending : EvmBlock
    public data object Safe : EvmBlock
    public data object Finalized : EvmBlock
    public data class Exact(val number: EvmNumber) : EvmBlock
}
