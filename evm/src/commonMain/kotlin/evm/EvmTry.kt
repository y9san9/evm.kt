package evm

/**
 * Explicit error handling. It adds boilerplate on call site that helps one to
 * always see where control flow can jump.
 *
 * **Intentionally does not support `map` and other functional operators**. This
 * is done to keep code intuitive and simple.
 *
 * Example of code:
 *
 * ```
 * fun main(): Try<Unit> {
 *     val user = getUser().or { return Try.Fail(it) }
 *     val chat = getChat(user).or { return Try.Fail(it) }
 *     val firstMessage = getFirstMessage(chat).or { return Try.Fail(it) }
 *     return Try.Ok(Unit)
 * }
 * ```
 *
 * Provides the same flexibility as error throwing, but keeps control flow
 * linear. Used in environments where robustness is main priority.
 */
public sealed interface EvmTry<out T> {
    public data class Ok<out T>(val value: T) : EvmTry<T>
    public data class Fail(val cause: Throwable) : EvmTry<Nothing> {
        override fun toString(): String = buildString {
            appendLine("Failure(")
            appendLine(cause.stackTraceToString().trim().prependIndent())
            append(")")
        }
    }
}

/**
 * Essentially achieves the same result as `or_return` in Odin
 */
public inline fun <T> EvmTry<T>.or(block: (cause: Throwable) -> T): T =
    when (this) {
        is EvmTry.Ok -> this.value
        is EvmTry.Fail -> block(this.cause)
    }
