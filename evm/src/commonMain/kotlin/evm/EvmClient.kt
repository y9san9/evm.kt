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
     * Unsafe EvmExecutor that throws EvmIO.Exception on method calls. Use [io]
     * when possible.
     */
    public val unsafe: EvmExecutor = EvmExecutor(endpoint, httpClient, json)

    public inline fun <T> io(block: (EvmExecutor) -> T): EvmIO<T> = try {
        EvmIO.Ok(block(unsafe))
    } catch (exception: EvmIO.Exception) {
        EvmIO.Fail(exception)
    }
}
