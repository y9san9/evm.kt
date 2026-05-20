package evm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public data class EvmResponseSerializable(
    val jsonrpc: String,
    val method: String,
    val params: List<JsonElement>,
)
