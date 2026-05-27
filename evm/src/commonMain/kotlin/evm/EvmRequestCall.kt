package evm

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

public class EvmRequestCall(
    private val call: EvmCall,
    private val block: EvmBlock,
) : EvmRequest<EvmHex> {
    override fun encode(json: Json): EvmRequestPayload {
        return EvmRequestPayload(
            method = "eth_call",
            params = listOf(
                json.encodeToJsonElement(call.serializable()),
                json.encodeToJsonElement(block.serializable()),
            ),
        )
    }

    // todo: what to do with insufficient balance
    override fun decode(json: Json, payload: EvmResponsePayload): EvmHex {
        return when (payload) {
            is Success -> {
                val serializable: EvmHexSerializable =
                    json.decodeFromJsonElement(payload.result)
                serializable.typed()
            }
            is EvmResponsePayload.Error -> payload.bang()
        }
    }
}
