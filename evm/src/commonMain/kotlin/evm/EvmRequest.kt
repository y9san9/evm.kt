package evm

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

/**
 * 3 types of accessors for request:
 *
 * * Direct function call
 * * Pass as an object for batching
 */
public interface EvmRequest<out T> {
    public fun encode(): EvmRequestSerializable
    public fun decode(response: EvmResponseSerializable): T
}

public class EvmRequestBuilder<T>(public val json: Json) {
    public var method: String? = null
    private val params: MutableList<JsonElement> = mutableListOf<JsonElement>()
    private var decoder: ((EvmResponseSerializable) -> T)? = null

    public inline fun <reified TParam> param(value: TParam) {
        param(value, json.serializersModule.serializer<TParam>())
    }

    public fun <TParam> param(
        value: TParam,
        serializer: SerializationStrategy<TParam>,
    ) {
        params += json.encodeToJsonElement(serializer, value)
    }

    public fun decoder(block: (EvmResponseSerializable) -> T) {
        decoder = block
    }

    public fun toRequest(): EvmRequest<T> {
        val method = method ?: error("Name is required for request")
        val params = params.toList()
        val decoder = decoder ?: error("Decoder is required for request")
        return object : EvmRequest<T> {
            override fun encode() = EvmRequestSerializable(
                jsonrpc = "2.0",
                method = method,
                params = params,
            )
            override fun decode(response: EvmResponseSerializable): T =
                decoder(response)
            override fun toString() =
                "EvmRequest(method=$method, params=$params)"
        }
    }
}

public inline fun <T> buildEvmRequest(
    json: Json,
    block: EvmRequestBuilder<T>.() -> Unit,
): EvmRequest<T> {
    val builder = EvmRequestBuilder<T>(json)
    builder.apply(block)
    return builder.toRequest()
}
