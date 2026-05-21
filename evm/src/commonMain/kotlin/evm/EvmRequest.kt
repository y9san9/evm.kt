package evm

import kotlin.reflect.KType

/**
 * Each request should have 2 accessors:
 *
 * * A direct function call on [EvmExecutor]
 * * Create an object for batching via [EvmRequests]
 */
public data class EvmRequest<out T>(
    public val method: String,
    public val params: List<Parameter>,
    public val handler: Handler<T>,
) {
    public data class Parameter(public val value: Any?, public val type: KType)

    // todo: handle errors
    public data class Handler<out T>(public val responseType: KType)
}
