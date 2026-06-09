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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
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

    public suspend fun <T> execute(
        requests: List<EvmRequest<T>>,
    ): List<T> {
        try {
            val requestIds = requests.associateBy { requestId.next() }
            val serializable = requestIds.map { (id, request) ->
                val payload = request.encode(json)
                EvmRequestSerializable(id, payload.method, payload.params)
            }
            val responses = httpClient
                .post { setBody(serializable) }
                .body<List<EvmResponseSerializable>>()
                .associate { response ->
                    response.id to response.toPayload()
                }
            val result = requestIds.map { (requestId, request) ->
                val serializable = responses.getValue(requestId)
                request.decode(json, serializable)
            }
            return result
        } catch (exception: Exception) {
            throw EvmIO.Exception(exception)
        }
    }
}

@Serializable
private data class EvmRequestSerializable(
    val jsonrpc: String,
    val id: Long,
    val method: String,
    val params: List<JsonElement>,
) {
    constructor(
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

@Serializable(with = EvmResponseSerializable.Serializer::class)
private sealed interface EvmResponseSerializable {
    val jsonrpc: String
    val id: Long

    fun toPayload(): EvmResponsePayload

    @Serializable
    data class Success(
        @SerialName("jsonrpc")
        override val jsonrpc: String,
        @SerialName("id")
        override val id: Long,
        @SerialName("result")
        val result: JsonElement,
    ) : EvmResponseSerializable {
        override fun toPayload(): EvmResponsePayload.Success =
            EvmResponsePayload.Success(result)
    }

    @Serializable
    data class Failure(
        @SerialName("jsonrpc")
        override val jsonrpc: String,
        @SerialName("id")
        override val id: Long,
        @SerialName("error")
        val error: Error,
    ) : EvmResponseSerializable {
        override fun toPayload(): EvmResponsePayload.Error =
            EvmResponsePayload.Error(
                code = error.code,
                message = error.message,
            )
    }

    @Serializable
    data class Error(
        @SerialName("code")
        val code: Long,
        @SerialName("message")
        val message: String,
    )

    @Suppress("ktlint:standard:max-line-length")
    object Serializer : JsonContentPolymorphicSerializer<EvmResponseSerializable>(
        EvmResponseSerializable::class,
    ) {
        override fun selectDeserializer(
            element: JsonElement,
        ): DeserializationStrategy<EvmResponseSerializable> = when {
            "error" in element.jsonObject -> Failure.serializer()
            else -> Success.serializer()
        }
    }
}

internal expect class EvmRequestId() {
    fun next(): Long
}
