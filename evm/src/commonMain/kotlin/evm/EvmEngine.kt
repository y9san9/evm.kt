package evm

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

// todo: doc
public class EvmEngine(
    private val endpoint: EvmUrl,
    private val httpClient: HttpClient,
    private val json: Json,
) {
    public suspend fun <T> execute(requests: List<EvmRequest<T>>): List<T> {
        TODO()
    }
}
