package evm

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

public class EvmRequestBlockNumber : EvmRequest<EvmNumber> {
    override fun encode(json: Json): EvmRequestPayload {
        return EvmRequestPayload(
            method = "eth_blockNumber",
            params = emptyList(),
        )
    }

    override fun decode(json: Json, payload: EvmResponsePayload): EvmNumber {
        payload as Success
        return json.decodeFromJsonElement<EvmHexSerializable>(payload.result).typed()
    }
}
