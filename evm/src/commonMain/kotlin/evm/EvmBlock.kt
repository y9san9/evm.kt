package evm

import kotlinx.serialization.Serializable

public sealed interface EvmBlock {
    public fun serializable(): EvmBlockSerializable

    public data object Latest : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "latest")
    }
    public data object Earlier : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = "earlier")
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
    public data class Exact(val number: EvmNumber) : EvmBlock {
        override fun serializable(): EvmBlockSerializable =
            EvmBlockSerializable(string = number.toHexString())
    }
}

@Serializable
@JvmInline
public value class EvmBlockSerializable(public val string: String) {
    public fun typed(): EvmBlock {
        return when (string) {
            "latest" -> Latest
            "earlier" -> Earlier
            "pending" -> Pending
            "safe" -> Safe
            "finalized"-> Finalized
            else -> EvmBlock.Exact(EvmNumber.fromHexOrThrow(string))
        }
    }
}
