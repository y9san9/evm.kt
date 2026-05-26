package evm

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

public class EvmRequestCall(
    private val call: EvmCall,
    private val block: EvmBlock,
) : EvmRequest<EvmEncoded> {
    override fun encode(json: Json): EvmRequestPayload {
        return EvmRequestPayload(
            method = "eth_call",
            params = listOf(
                json.encodeToJsonElement(call.serializable()),
                json.encodeToJsonElement(block.serializable()),
            ),
        )
    }

    override fun decode(json: Json, payload: EvmResponsePayload): EvmEncoded {
        TODO()
    }
}
