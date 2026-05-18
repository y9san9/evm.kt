package evm

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

public class EvmExecutor internal constructor(
    private val endpoint: EvmUrl,
    private val httpClient: HttpClient,
    private val json: Json,
) {
    private val engine = EvmEngine(endpoint, httpClient, json)

    public suspend fun getBalance() {
    }
}
