package evm

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Each request should have 2 accessors:
 *
 * * A direct function call on [EvmExecutor]
 * * Create an object for batching via [EvmRequests]
 *
 * This class is a command to [EvmEngine] so that [EvmEngine] has enough
 * information on what should be done.
 */
public interface EvmRequest<out T> {
    public fun encode(json: Json): EvmRequestPayload
    public fun decode(json: Json, payload: EvmResponsePayload): T
}

public data class EvmRequestPayload(
    val method: String,
    val params: List<JsonElement>,
)

public sealed interface EvmResponsePayload {
    public data class Error(val code: Long, val message: String) :
        EvmResponsePayload {
        public fun bang(): Nothing = throw Exception(code, message)
    }

    public data class Success(val result: JsonElement) : EvmResponsePayload

    public class Exception(
        public val code: Long,
        override val message: String,
    ) : RuntimeException("EvmRequest.Error(code=$code,message=$message)")
}
