package evm

import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public class EvmRequestBuilder<T> {
    public var method: String? = null
    public val params: MutableList<EvmRequest.Parameter> = mutableListOf()
    public var handler: Handler<T> = Handler()

    public fun handler(block: Handler<T>.() -> Unit) {
        handler.apply(block)
    }

    public inline fun <reified T> param(value: T) {
        params += EvmRequest.Parameter(value, typeOf<T>())
    }

    public class Handler<T> {
        public var responseType: KType? = null

        public fun build(): EvmRequest.Handler<T> {
            val responseType = this.responseType
                ?: error("Provide 'responseType' for request")
            return EvmRequest.Handler(responseType)
        }
    }

    public fun build(): EvmRequest<T> {
        val method = this.method ?: error("Provide 'method' for request")
        return EvmRequest(method, params.toList(), handler.build())
    }
}

public inline fun <T> buildEvmRequest(
    json: Json,
    block: EvmRequestBuilder<T>.() -> Unit,
): EvmRequest<T> {
    val builder = EvmRequestBuilder<T>()
    builder.apply(block)
    return builder.build()
}

public inline fun <reified T> EvmRequestBuilder.Handler<T>.responseType() {
    responseType = typeOf<T>()
}
