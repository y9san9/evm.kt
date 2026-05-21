package evm

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

/**
 * A low-level class that abstracts away jsonrpc implementation details.
 */
public class EvmEngine(
    private val endpoint: EvmUrl,
    private val json: Json,
    httpClient: HttpClient,
) {
    private val httpClient = httpClient.config {
        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url(endpoint.string)
            contentType(ContentType.Application.Json)
        }
    }

    private val requestId = EvmRequestId()

    public suspend fun <T> execute(requests: List<EvmRequest<T>>): List<T> {
        try {
            val requestIds = requests.associateBy { requestId.next() }
            val serializable = requestIds.map { (id, request) ->
                EvmRequestPayload(
                    id = id,
                    method = request.method,
                    params = request.params.map { (value, type) ->
                        val serializer = json.serializersModule.serializer(type)
                        json.encodeToJsonElement(serializer, value)
                    },
                )
            }
            val responses = httpClient
                .post { setBody(serializable) }
                .body<List<EvmResponsePayload>>()
                .associateBy { response -> response.id }
            return requestIds.map { (requestId, request) ->
                val payload = responses.getValue(requestId)
                val type = request.handler.responseType

                @Suppress("UNCHECKED_CAST")
                val serializer = json.serializersModule.serializer(
                    type,
                ) as KSerializer<T>
                json.decodeFromJsonElement(serializer, payload.result)
            }
        } catch (exception: IOException) {
            throw EvmIO.Exception(exception)
        }
    }
}

@Serializable
public data class EvmRequestPayload(
    val jsonrpc: String,
    val id: Long,
    val method: String,
    val params: List<JsonElement>,
) {
    public constructor(
        id: Long,
        method: String,
        params: List<JsonElement>,
    ) : this(
        jsonrpc = "2.0",
        id = id,
        method = method,
        params = params,
    )
}

@Serializable
public data class EvmResponsePayload(
    val jsonrpc: String,
    val id: Long,
    val result: JsonElement,
)

internal expect class EvmRequestId() {
    fun next(): Long
}
