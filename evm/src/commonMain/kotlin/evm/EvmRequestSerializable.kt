package evm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class EvmRequestSerializable(
    val jsonrpc: String,
    val method: String,
    val params: List<JsonElement>,
)
