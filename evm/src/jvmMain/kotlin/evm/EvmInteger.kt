@file:JvmName("JvmEvmIntegerKt")

package evm

public actual class EvmInteger(private val underlying: java.math.BigInteger) :
    Comparable<EvmInteger> {
    public actual constructor(int: Int) : this(int.toLong())
    public actual constructor(
        long: Long,
    ) : this(java.math.BigInteger.valueOf(long))

    public actual operator fun unaryMinus(): EvmInteger =
        EvmInteger(underlying.negate())

    public actual operator fun unaryPlus(): EvmInteger = this

    public actual operator fun times(number: EvmInteger): EvmInteger {
        val underlying = this.underlying.multiply(number.underlying)
        return EvmInteger(underlying)
    }

    public actual operator fun minus(number: EvmInteger): EvmInteger {
        val underlying = this.underlying.subtract(number.underlying)
        return EvmInteger(underlying)
    }

    public actual operator fun plus(number: EvmInteger): EvmInteger {
        val underlying = this.underlying.add(number.underlying)
        return EvmInteger(underlying)
    }

    public actual fun divOrThrow(number: EvmInteger): EvmInteger {
        val underlying = this.underlying.divide(number.underlying)
        return EvmInteger(underlying)
    }

    public actual fun remOrThrow(number: EvmInteger): EvmInteger {
        val underlying = this.underlying.divideAndRemainder(number.underlying)
        return EvmInteger(underlying[1])
    }

    public actual fun pow(int: Int): EvmInteger {
        val underlying = this.underlying.pow(int)
        return EvmInteger(underlying)
    }

    public actual fun serializable(): EvmIntegerSerializable =
        EvmIntegerSerializable(toHexString())

    actual override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EvmInteger) return false
        return compareTo(other) == 0
    }

    actual override fun hashCode(): Int = underlying.hashCode()

    actual override fun compareTo(other: EvmInteger): Int =
        this.underlying.compareTo(other.underlying)

    actual override fun toString(): String = underlying.toString()

    public actual fun toHexString(): String {
        val string = underlying.toString(16)
        return "0x$string"
    }

    public actual companion object {
        public actual val Zero: EvmInteger
            get() = EvmInteger(java.math.BigInteger.ZERO)

        public actual val One: EvmInteger
            get() = EvmInteger(java.math.BigInteger.ONE)

        public actual val Ten: EvmInteger
            get() = EvmInteger(java.math.BigInteger.TEN)

        public actual fun orThrow(string: String): EvmInteger {
            val underlying = java.math.BigInteger(string)
            return EvmInteger(underlying)
        }

        public actual fun fromHexStringOrThrow(string: String): EvmInteger {
            require(string.startsWith("0x"))
            return EvmInteger(java.math.BigInteger(string.drop(2), 16))
        }

        public actual fun fromHexUnsignedBigEndian(hex: EvmHex): EvmInteger =
            EvmInteger(java.math.BigInteger(1, hex.unsafeBytes))

        public actual fun maxValueUnsigned(sizeBytes: Int): EvmInteger =
            EvmInteger(2).pow(sizeBytes * 8) - 1.evmInteger
    }
}
