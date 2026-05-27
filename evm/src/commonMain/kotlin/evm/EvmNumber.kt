package evm

import kotlinx.serialization.Serializable

public expect fun EvmNumber(hex: EvmHex): EvmNumber

public expect class EvmNumber : Comparable<EvmNumber> {
    public constructor(int: Int)
    public constructor(long: Long)

    public val scale: Int

    public fun pow(power: Int): EvmNumber

    public operator fun unaryMinus(): EvmNumber

    public operator fun unaryPlus(): EvmNumber

    public operator fun plus(number: EvmNumber): EvmNumber

    public operator fun minus(number: EvmNumber): EvmNumber

    public operator fun times(number: EvmNumber): EvmNumber

    public fun divOrThrow(number: EvmNumber, scale: Int): EvmNumber

    public fun remOrThrow(number: EvmNumber): EvmNumber

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    override fun compareTo(other: EvmNumber): Int

    override fun toString(): String
    public fun hex(): EvmHex

    public fun toPlainString(): String

    public fun serializable(): EvmNumberSerializable

    public companion object {
        public val Zero: EvmNumber
        public val One: EvmNumber
        public val Ten: EvmNumber

        public fun orThrow(string: String): EvmNumber
    }
}

public fun EvmNumber.half(): EvmNumber = divOrThrow(
    number = 2.bigNumber,
    scale =
    this.scale + 1,
)

public val Int.bigNumber: EvmNumber
    get() = EvmNumber(int = this)

public operator fun EvmNumber.plus(int: Int): EvmNumber = this + EvmNumber(int)

public operator fun EvmNumber.minus(int: Int): EvmNumber = this - EvmNumber(int)

public operator fun EvmNumber.times(int: Int): EvmNumber = this * EvmNumber(int)

public fun EvmNumber.divOrThrow(int: Int, scale: Int): EvmNumber =
    this.divOrThrow(EvmNumber(int), scale)

public fun EvmNumber.remOrThrow(int: Int): EvmNumber =
    this.remOrThrow(EvmNumber(int))

public val Long.bigNumber: EvmNumber
    get() = EvmNumber(long = this)

public operator fun EvmNumber.plus(long: Long): EvmNumber =
    this + EvmNumber(long)

public operator fun EvmNumber.minus(long: Long): EvmNumber =
    this - EvmNumber(long)

public operator fun EvmNumber.times(long: Long): EvmNumber =
    this * EvmNumber(long)

public fun EvmNumber.divOrThrow(long: Long, scale: Int): EvmNumber =
    this.divOrThrow(EvmNumber(long), scale)

public fun EvmNumber.remOrThrow(long: Long): EvmNumber =
    this.remOrThrow(EvmNumber(long))

@Serializable
@JvmInline
public value class EvmNumberSerializable(public val string: String) {
    init {
        EvmNumber.orThrow(string)
    }

    public fun typed(): EvmNumber = EvmNumber.orThrow(string)
}
