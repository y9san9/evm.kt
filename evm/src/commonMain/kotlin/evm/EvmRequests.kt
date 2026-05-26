package evm

import kotlinx.serialization.json.Json

/**
 * Class that has all request definitions inside of it. It's pure, it doesn't go
 * to the Internet and does not perform any IO.
 */
public class EvmRequests(private val json: Json) {
    public fun getBlockNumber(): EvmRequest<EvmNumber> =
        EvmRequestBlockNumber()
    public fun call(call: EvmCall, block: EvmBlock): EvmRequest<EvmEncoded> =
        EvmRequestCall(call, block)
}
