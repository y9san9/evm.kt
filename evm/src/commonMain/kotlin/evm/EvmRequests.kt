package evm

import kotlinx.serialization.json.Json

/**
 * Class that has all request definitions inside of it. It's pure, it doesn't go
 * to the Internet and does not perform any IO.
 */
public class EvmRequests(private val json: Json) {
    public fun getBlockNumber(): EvmRequest<EvmBigNumber> = buildEvmRequest {
        method = "eth_blockNumber"
        handler {
            responseType<EvmBigNumber>()
        }
    }

    private inline fun <T> buildEvmRequest(
        block: EvmRequestBuilder<T>.() -> Unit,
    ): EvmRequest<T> = buildEvmRequest(json, block)
}
