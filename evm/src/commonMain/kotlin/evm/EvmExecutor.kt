package evm

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

public class EvmExecutor(
    private val endpoint: EvmUrl,
    private val httpClient: HttpClient,
    private val json: Json,
) {
    private val engine = EvmEngine(endpoint, json, httpClient)

    public val requests: EvmRequests = EvmRequests(json)

    public suspend fun getBlockNumber(): EvmHex =
        execute(requests.getBlockNumber())

    public suspend fun call(call: EvmCall, block: EvmBlock): EvmCallResult =
        execute(requests.call(call, block))

    public suspend fun <T> execute(requests: List<EvmRequest<T>>): List<T> =
        engine.execute(requests)

    public suspend fun <T> execute(vararg requests: EvmRequest<T>): List<T> =
        execute(requests.toList())

    public suspend fun <T> execute(request: EvmRequest<T>): T {
        val (response) = execute(listOf(request))
        @Suppress("UNCHECKED_CAST")
        return response as T
    }
}
