package evm

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

public class EvmRequestBlockNumber : EvmRequest<EvmHex> {
    override fun encode(json: Json): EvmRequestPayload = EvmRequestPayload(
        method = "eth_blockNumber",
        params = emptyList(),
    )

    override fun decode(json: Json, payload: EvmResponsePayload): EvmHex {
        payload as Success
        return json.decodeFromJsonElement<EvmHexSerializable>(
            payload.result,
        ).typed()
    }
}
