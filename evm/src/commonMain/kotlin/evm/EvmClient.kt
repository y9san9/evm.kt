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
    /**
     * Pefer using [io] whenever possible
     */
    public val unsafe: EvmExecutor = EvmExecutor(endpoint, httpClient, json)

    /**
     * An explicit way to create IO gateway.
     *
     * Example:
     *
     * val client = EvmClient(...)
     * val result = client.use { executor ->
     *     executor.getBalance()
     * }
     */
    public inline fun <T> use(block: (EvmExecutor) -> T): EvmIO<T> = try {
        EvmIO.Success(block(unsafe))
    } catch (exception: EvmIO.Exception) {
        EvmIO.Error(EvmIO.Exception(exception))
    }
}
