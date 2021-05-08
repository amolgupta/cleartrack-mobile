package xyz.getclear.data.utils

import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@InternalSerializationApi
@ExperimentalSerializationApi
@Serializer(forClass = LocalDate::class)
object DateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("DateSerializer", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(
            value.atStartOfDayIn(TimeZone.UTC).toString()
        )
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return Instant.parse(decoder.decodeString()).toLocalDateTime(TimeZone.UTC).date
    }
}