package evm

import kotlin.reflect.KType

/**
 * Each request should have 2 accessors:
 *
 * * A direct function call on [EvmExecutor]
 * * Create an object for batching via [EvmRequests]
 *
 * This class is a command to [EvmEngine] so that [EvmEngine] has enough
 * information on what should be done.
 */
public data class EvmRequest<out T>(
    public val method: String,
    public val params: List<Parameter>,
    public val handler: Handler<T>,
) {
    public data class Parameter(public val value: Any?, public val type: KType)

    // todo: handle errors
    public data class Handler<out T>(
        public val responseSerializableType: KType,
        public val responseConverter: ResponseConverter<Any?, T>,
    )

    public interface ResponseConverter<in TSerializable, out T> {
        public fun convert(serializable: TSerializable): T

        public object None : ResponseConverter<Any?, Any?> {
            override fun convert(serializable: Any?): Any? = serializable
        }

        public companion object {
            @Suppress("UNCHECKED_CAST")
            public fun <T> none(): ResponseConverter<T, T> =
                None as ResponseConverter<T, T>
        }
    }
}
