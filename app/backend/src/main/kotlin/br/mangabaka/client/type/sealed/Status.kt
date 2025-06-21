package br.mangabaka.client.type.sealed

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = StatusSerializer::class)
sealed class Status {
    object Finished : Status()
    object Releasing : Status()
    object NotYetReleased : Status()
    object Cancelled : Status()
    data class Unknown(val rawValue: String) : Status()
}

object StatusSerializer : KSerializer<Status> {
    override val descriptor = PrimitiveSerialDescriptor("Status", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Status {
        val raw = decoder.decodeString()
        return when (raw.uppercase()) {
            "FINISHED" -> Status.Finished
            "RELEASING" -> Status.Releasing
            "NOT_YET_RELEASED" -> Status.NotYetReleased
            "CANCELLED" -> Status.Cancelled
            else -> Status.Unknown(raw)
        }
    }

    override fun serialize(encoder: Encoder, value: Status) {
        val string = when (value) {
            Status.Finished -> "FINISHED"
            Status.Releasing -> "RELEASING"
            Status.NotYetReleased -> "NOT_YET_RELEASED"
            Status.Cancelled -> "CANCELLED"
            is Status.Unknown -> value.rawValue
        }
        encoder.encodeString(string)
    }
}