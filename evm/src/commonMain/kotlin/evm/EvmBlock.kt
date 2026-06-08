package evm

import kotlinx.serialization.Serializable

public sealed interface EvmBlock {
    public fun serializable(): EvmBlockSerializable

    public data object Latest : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "latest")
    }
    public data object Earliest : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "earliest")
    }
    public data object Pending : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "pending")
    }
    public data object Safe : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "safe")
    }
    public data object Finalized : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "finalized")
    }
    public data class Exact(val hex: EvmHex) : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = hex.serializable().string)
    }
}

@Serializable
@JvmInline
public value class EvmBlockSerializable(public val string: String) {
    public fun typed(): EvmBlock = when (string) {
        "latest" -> Latest
        "earliest" -> Earliest
        "pending" -> Pending
        "safe" -> Safe
        "finalized" -> Finalized
        else -> EvmBlock.Exact(EvmHexSerializable(string).typed())
    }
}
