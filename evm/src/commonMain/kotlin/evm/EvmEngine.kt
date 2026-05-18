package evm

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

public class EvmEngine(
    private val endpoint: EvmUrl,
    private val httpClient: HttpClient,
    private val json: Json,
)
