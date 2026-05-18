package evm

public sealed interface EvmIO<out T> {
    public fun bang(): T

    public data class Success<out T>(
        val value: T,
    ) : EvmIO<T> {
        override fun bang(): T = value
    }

    public data class Error(
        val cause: Throwable,
    ) : EvmIO<Nothing> {
        override fun bang(): Nothing = throw Exception(cause)
    }

    public data class Exception(
        override val cause: Throwable,
    ) : RuntimeException()
}
