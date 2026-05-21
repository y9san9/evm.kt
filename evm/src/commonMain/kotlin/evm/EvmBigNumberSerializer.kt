package evm

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalSerializationApi::class)
public object EvmBigNumberSerializer : KSerializer<EvmBigNumber> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            serialName = "evm.EvmBigNumber",
            kind = PrimitiveKind.STRING,
        )

    override fun deserialize(decoder: Decoder): EvmBigNumber {
        decoder as JsonDecoder
        val string = decoder.decodeJsonElement().jsonPrimitive.content
        if (string.startsWith("0x")) {
            return convertBase16ToBase10(string.drop(2))
        }
        return EvmBigNumber.orThrow(string)
    }

    override fun serialize(encoder: Encoder, value: EvmBigNumber) {
        encoder as JsonEncoder
        val literal = JsonUnquotedLiteral(value.toPlainString())
        encoder.encodeJsonElement(literal)
    }
}

internal expect fun convertBase16ToBase10(string: String): EvmBigNumber
