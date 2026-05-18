package evm

import kotlinx.serialization.Serializable
import java.math.RoundingMode

@Serializable(EvmBigNumberSerializer::class)
public actual class EvmBigNumber(
    private val underlying: java.math.BigDecimal,
) : Comparable<EvmBigNumber> {
    public actual constructor(int: Int) : this(java.math.BigDecimal(int))

    public actual constructor(long: Long) : this(java.math.BigDecimal(long))

    public actual val scale: Int
        get() = underlying.scale()

    public actual fun pow(power: Int): EvmBigNumber {
        val underlying = this.underlying.pow(power)
        return EvmBigNumber(underlying)
    }

    public actual operator fun unaryMinus(): EvmBigNumber = EvmBigNumber(underlying.negate())

    public actual operator fun unaryPlus(): EvmBigNumber = this

    public actual operator fun times(number: EvmBigNumber): EvmBigNumber {
        val underlying = this.underlying.multiply(number.underlying)
        return EvmBigNumber(underlying)
    }

    public actual operator fun minus(number: EvmBigNumber): EvmBigNumber {
        val underlying = this.underlying.subtract(number.underlying)
        return EvmBigNumber(underlying)
    }

    public actual operator fun plus(number: EvmBigNumber): EvmBigNumber {
        val underlying = this.underlying.add(number.underlying)
        return EvmBigNumber(underlying)
    }

    public actual fun divOrThrow(
        number: EvmBigNumber,
        scale: Int,
    ): EvmBigNumber {
        val underlying =
            this.underlying.divide(
                number.underlying,
                scale,
                RoundingMode.HALF_EVEN,
            )
        return EvmBigNumber(underlying)
    }

    public actual fun remOrThrow(number: EvmBigNumber): EvmBigNumber {
        val underlying = this.underlying.divideAndRemainder(number.underlying)
        return EvmBigNumber(underlying[1])
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EvmBigNumber) return false
        return compareTo(other) == 0
    }

    actual override fun compareTo(other: EvmBigNumber): Int = this.underlying.compareTo(other.underlying)

    actual override fun toString(): String = underlying.toString()

    public actual fun toPlainString(): String = underlying.toPlainString()

    public actual companion object {
        public actual val Zero: EvmBigNumber
            get() = EvmBigNumber(java.math.BigDecimal.ZERO)

        public actual val One: EvmBigNumber
            get() = EvmBigNumber(java.math.BigDecimal.ONE)

        public actual val Ten: EvmBigNumber
            get() = EvmBigNumber(java.math.BigDecimal.TEN)

        public actual fun orThrow(string: String): EvmBigNumber {
            val underlying = java.math.BigDecimal(string)
            return EvmBigNumber(underlying)
        }
    }

    actual override fun hashCode(): Int = underlying.stripTrailingZeros().hashCode()
}
