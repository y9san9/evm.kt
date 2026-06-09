package evm

public sealed interface EvmIO<out T> {
    public data class Ok<out T>(val value: T) : EvmIO<T>
    public data class Fail(val cause: Throwable) : EvmIO<Nothing> {
        override fun toString(): String = buildString {
            appendLine("Failure(")
            appendLine(cause.stackTraceToString().trim().prependIndent())
            append(")")
        }
    }
    public class Exception(override val cause: Throwable) : RuntimeException()
}
