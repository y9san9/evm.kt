package evm

internal actual fun convertBase16ToBase10(string: String): EvmBigNumber {
    val integer = java.math.BigInteger(string, 16)
    return EvmBigNumber.orThrow(integer.toString())
}
