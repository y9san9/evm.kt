package evm

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

// todo: doc
public class EvmExecutor internal constructor(
    private val endpoint: EvmUrl,
    private val httpClient: HttpClient,
    private val json: Json,
) {
    private val engine = EvmEngine(endpoint, httpClient, json)

    public val requests: EvmRequests = EvmRequests(json)

    public suspend fun <T> execute(requests: List<EvmRequest<T>>): List<T> =
        engine.execute(requests)

    public suspend fun <T> execute(vararg requests: EvmRequest<T>): List<T> =
        execute(requests.toList())

    public suspend fun <T> execute(request1: EvmRequest<T>): T {
        val (response1) = execute(listOf(request1))
        @Suppress("UNCHECKED_CAST")
        return response1 as T
    }

    public suspend fun getBalance() {
        execute(requests.getBalance())
    }
}
