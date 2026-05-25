package evm

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
        public var responseSerializableType: KType? = null
        public var responseConverter: EvmRequest.ResponseConverter<Any?, T>? = null

        public fun build(): EvmRequest.Handler<T> {
            val responseSerializableType = this.responseSerializableType
                ?: error("Provide 'responseSerializableType' for request")
            val responseConverter = this.responseConverter
                ?: error("Provide 'responseConverter' for request")
            return EvmRequest.Handler(
                responseSerializableType = responseSerializableType,
                responseConverter = responseConverter,
            )
        }
    }

    public fun build(): EvmRequest<T> {
        val method = this.method ?: error("Provide 'method' for request")
        return EvmRequest(method, params.toList(), handler.build())
    }
}

public inline fun <T> buildEvmRequest(
    block: EvmRequestBuilder<T>.() -> Unit,
): EvmRequest<T> {
    val builder = EvmRequestBuilder<T>()
    builder.apply(block)
    return builder.build()
}

public inline fun <reified TSerializable, T> EvmRequestBuilder.Handler<T>.response(
    converter: EvmRequest.ResponseConverter<TSerializable, T>,
) {
    responseSerializableType = typeOf<TSerializable>()
    @Suppress("UNCHECKED_CAST")
    responseConverter = converter as EvmRequest.ResponseConverter<Any?, T>
}

public inline fun <reified T> EvmRequestBuilder.Handler<T>.responseType() {
    responseSerializableType = typeOf<T>()
    @Suppress("UNCHECKED_CAST")
    responseConverter =
        EvmRequest.ResponseConverter.none<T>() as EvmRequest.ResponseConverter<Any?, T>
}
