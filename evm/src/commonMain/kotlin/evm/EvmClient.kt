package evm

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

public fun EvmClient(endpoint: String): EvmClient = EvmClient(
    endpoint = EvmUrl(endpoint),
    httpClient = HttpClient(),
    json = Json,
)

/**
 * Entry point that holds config to use this library. Library code was written
 * in a way that it is easily understandable if you start inspecting it from
 * here.
 */
public class EvmClient(
    private val endpoint: EvmUrl,
    private val httpClient: HttpClient,
    private val json: Json,
) {
    private val engine = EvmEngine(endpoint, json, httpClient)

    public val requests: EvmRequests = EvmRequests(json)

    public suspend fun getBlockNumber(): EvmTry<EvmHex> =
        execute(requests.getBlockNumber())

    public suspend fun call(
        call: EvmCall,
        block: EvmBlock,
    ): EvmTry<EvmCallResult> = execute(requests.call(call, block))

    public suspend fun <T> execute(
        requests: List<EvmRequest<T>>,
    ): EvmTry<List<T>> = engine.execute(requests)

    public suspend fun <T> execute(
        vararg requests: EvmRequest<T>,
    ): EvmTry<List<T>> = execute(requests.toList())

    public suspend fun <T> execute(request: EvmRequest<T>): EvmTry<T> {
        val (response) = execute(listOf(request)).or {
            return EvmTry.Fail(it)
        }
        @Suppress("UNCHECKED_CAST")
        return EvmTry.Ok(response as T)
    }
}
