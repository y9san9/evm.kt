package evm

import kotlinx.serialization.Serializable

public expect class EvmInteger : Comparable<EvmInteger> {
    public constructor(int: Int)
    public constructor(long: Long)

    public operator fun unaryMinus(): EvmInteger
    public operator fun unaryPlus(): EvmInteger
    public operator fun plus(number: EvmInteger): EvmInteger
    public operator fun minus(number: EvmInteger): EvmInteger
    public operator fun times(number: EvmInteger): EvmInteger

    public fun divOrThrow(number: EvmInteger): EvmInteger
    public fun remOrThrow(number: EvmInteger): EvmInteger

    public fun pow(int: Int): EvmInteger

    public fun serializable(): EvmIntegerSerializable

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int

    override fun compareTo(other: EvmInteger): Int

    override fun toString(): String
    public fun toHexString(): String

    public companion object {
        public val Zero: EvmInteger
        public val One: EvmInteger
        public val Ten: EvmInteger

        public fun orThrow(string: String): EvmInteger
        public fun fromHexStringOrThrow(string: String): EvmInteger
        public fun fromHexUnsignedBigEndian(hex: EvmHex): EvmInteger

        public fun maxValueUnsigned(sizeBytes: Int): EvmInteger
    }
}

@JvmInline
@Serializable
public value class EvmIntegerSerializable(public val string: String) {
    public fun typed(): EvmInteger = EvmInteger.fromHexStringOrThrow(string)
}

public val Int.evmInteger: EvmInteger
    get() = EvmInteger(int = this)

public val Long.evmInteger: EvmInteger
    get() = EvmInteger(long = this)
