package evm

import kotlinx.serialization.Serializable

@Serializable(EvmBigNumberSerializer::class)
public expect class EvmBigNumber : Comparable<EvmBigNumber> {
    public constructor(int: Int)
    public constructor(long: Long)

    public val scale: Int

    public fun pow(power: Int): EvmBigNumber

    public operator fun unaryMinus(): EvmBigNumber

    public operator fun unaryPlus(): EvmBigNumber

    public operator fun plus(number: EvmBigNumber): EvmBigNumber

    public operator fun minus(number: EvmBigNumber): EvmBigNumber

    public operator fun times(number: EvmBigNumber): EvmBigNumber

    public fun divOrThrow(number: EvmBigNumber, scale: Int): EvmBigNumber

    public fun remOrThrow(number: EvmBigNumber): EvmBigNumber

    override fun equals(other: Any?): Boolean

    override fun compareTo(other: EvmBigNumber): Int

    override fun toString(): String

    public fun toPlainString(): String

    public companion object {
        public val Zero: EvmBigNumber
        public val One: EvmBigNumber
        public val Ten: EvmBigNumber

        public fun orThrow(string: String): EvmBigNumber
    }

    override fun hashCode(): Int
}

public fun EvmBigNumber.half(): EvmBigNumber = divOrThrow(
    number = 2.bigNumber,
    scale =
    this.scale + 1,
)

public val Int.bigNumber: EvmBigNumber
    get() = EvmBigNumber(int = this)

public operator fun EvmBigNumber.plus(int: Int): EvmBigNumber =
    this + EvmBigNumber(int)

public operator fun EvmBigNumber.minus(int: Int): EvmBigNumber =
    this - EvmBigNumber(int)

public operator fun EvmBigNumber.times(int: Int): EvmBigNumber =
    this * EvmBigNumber(int)

public fun EvmBigNumber.divOrThrow(int: Int, scale: Int): EvmBigNumber =
    this.divOrThrow(EvmBigNumber(int), scale)

public fun EvmBigNumber.remOrThrow(int: Int): EvmBigNumber =
    this.remOrThrow(EvmBigNumber(int))

public val Long.bigNumber: EvmBigNumber
    get() = EvmBigNumber(long = this)

public operator fun EvmBigNumber.plus(long: Long): EvmBigNumber =
    this + EvmBigNumber(long)

public operator fun EvmBigNumber.minus(long: Long): EvmBigNumber =
    this - EvmBigNumber(long)

public operator fun EvmBigNumber.times(long: Long): EvmBigNumber =
    this * EvmBigNumber(long)

public fun EvmBigNumber.divOrThrow(long: Long, scale: Int): EvmBigNumber =
    this.divOrThrow(EvmBigNumber(long), scale)

public fun EvmBigNumber.remOrThrow(long: Long): EvmBigNumber =
    this.remOrThrow(EvmBigNumber(long))
