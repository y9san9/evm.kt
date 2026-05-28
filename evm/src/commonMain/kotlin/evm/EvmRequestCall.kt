package evm

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

public class EvmRequestCall(
    private val call: EvmCall,
    private val block: EvmBlock,
) : EvmRequest<EvmHex> {
    override fun encode(json: Json): EvmRequestPayload = EvmRequestPayload(
        method = "eth_call",
        params = listOf(
            json.encodeToJsonElement(call.serializable()),
            json.encodeToJsonElement(block.serializable()),
        ),
    )

    // todo: what to do with insufficient balance
    override fun decode(json: Json, payload: EvmResponsePayload): EvmHex =
        when (payload) {
            is Success -> {
                val serializable: EvmHexSerializable =
                    json.decodeFromJsonElement(payload.result)
                serializable.typed()
            }
            is EvmResponsePayload.Error -> payload.bang()
        }
}
