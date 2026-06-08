package evm

public typealias EvmIOUnit = EvmIO<Unit>

/**
 * Explicit way of handling IO exceptions. The most robust and simple way of
 * doing it.
 */
public sealed interface EvmIO<out T> {
    public data class Success<out T>(val value: T) : EvmIO<T>
    public data class Failure(val cause: Throwable) : EvmIO<Nothing>

    public companion object {
        public val unit: EvmIOUnit = EvmIO.Success(Unit)
    }
}

/**
 * Essentially achieves the same result as `or_return` in Odin
 */
public inline fun <T> EvmIO<T>.or(block: (EvmIO.Failure) -> T): T =
    when (this) {
        is EvmIO.Success -> value
        is EvmIO.Failure -> block(this)
    }
