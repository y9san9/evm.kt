@file:JvmName("JvmEvmNumberKt")

package evm

import java.math.RoundingMode

public actual fun EvmNumber(hex: EvmHex): EvmNumber {
    val integer = java.math.BigInteger(1, hex.unsafeBytes)
    return EvmNumber(java.math.BigDecimal(integer))
}

public actual class EvmNumber(private val underlying: java.math.BigDecimal) :
    Comparable<EvmNumber> {
    public actual constructor(int: Int) : this(java.math.BigDecimal(int))

    public actual constructor(long: Long) : this(java.math.BigDecimal(long))

    public actual val scale: Int
        get() = underlying.scale()

    public actual fun pow(power: Int): EvmNumber {
        val underlying = this.underlying.pow(power)
        return EvmNumber(underlying)
    }

    public actual operator fun unaryMinus(): EvmNumber =
        EvmNumber(underlying.negate())

    public actual operator fun unaryPlus(): EvmNumber = this

    public actual operator fun times(number: EvmNumber): EvmNumber {
        val underlying = this.underlying.multiply(number.underlying)
        return EvmNumber(underlying)
    }

    public actual operator fun minus(number: EvmNumber): EvmNumber {
        val underlying = this.underlying.subtract(number.underlying)
        return EvmNumber(underlying)
    }

    public actual operator fun plus(number: EvmNumber): EvmNumber {
        val underlying = this.underlying.add(number.underlying)
        return EvmNumber(underlying)
    }

    public actual fun divOrThrow(number: EvmNumber, scale: Int): EvmNumber {
        val underlying =
            this.underlying.divide(
                number.underlying,
                scale,
                RoundingMode.HALF_EVEN,
            )
        return EvmNumber(underlying)
    }

    public actual fun remOrThrow(number: EvmNumber): EvmNumber {
        val underlying = this.underlying.divideAndRemainder(number.underlying)
        return EvmNumber(underlying[1])
    }

    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EvmNumber) return false
        return compareTo(other) == 0
    }

    actual override fun hashCode(): Int =
        underlying.stripTrailingZeros().hashCode()

    actual override fun compareTo(other: EvmNumber): Int =
        this.underlying.compareTo(other.underlying)

    actual override fun toString(): String = underlying.toString()

    public actual fun hex(): EvmHex {
        if (underlying.scale() > 0) {
            error(
                "Hex strings for decimal values are not supported at the moment",
            )
        }
        val string = underlying.toBigInteger().toString(16)
        val paddedString = if (string.length % 2 == 1) "0$string" else string
        return EvmHex.orThrow("0x$paddedString")
    }

    public actual fun toPlainString(): String = underlying.toPlainString()

    public actual fun serializable(): EvmNumberSerializable =
        EvmNumberSerializable(toString())

    public actual companion object {
        public actual val Zero: EvmNumber
            get() = EvmNumber(java.math.BigDecimal.ZERO)

        public actual val One: EvmNumber
            get() = EvmNumber(java.math.BigDecimal.ONE)

        public actual val Ten: EvmNumber
            get() = EvmNumber(java.math.BigDecimal.TEN)

        public actual fun orThrow(string: String): EvmNumber {
            val underlying = java.math.BigDecimal(string)
            return EvmNumber(underlying)
        }
    }
}
