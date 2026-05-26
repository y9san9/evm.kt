package evm

import kotlinx.serialization.Serializable

public data class EvmSignature(val number: EvmNumber) {
    public fun serializable(): EvmSignatureSerializable {
        return EvmSignatureSerializable(string = number.toHexString())
    }
}

@Serializable
@JvmInline
public value class EvmSignatureSerializable(public val string: String) {
    public fun typed(): EvmSignature {
        return EvmSignature(number = EvmNumber.fromHexOrThrow(string))
    }
}
