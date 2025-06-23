/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = StatusSerializer::class)
sealed class StatusManga {
    object Finished : StatusManga()
    object Releasing : StatusManga()
    object NotYetReleased : StatusManga()
    object Cancelled : StatusManga()
    data class Unknown(val rawValue: String) : StatusManga()
}

object StatusSerializer : KSerializer<StatusManga> {
    override val descriptor = PrimitiveSerialDescriptor("Status", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): StatusManga {
        val raw = decoder.decodeString()
        return when (raw.uppercase()) {
            "FINISHED" -> StatusManga.Finished
            "RELEASING" -> StatusManga.Releasing
            "NOT_YET_RELEASED" -> StatusManga.NotYetReleased
            "CANCELLED" -> StatusManga.Cancelled
            else -> StatusManga.Unknown(raw)
        }
    }

    override fun serialize(encoder: Encoder, value: StatusManga) {
        val string = when (value) {
            StatusManga.Finished -> "FINISHED"
            StatusManga.Releasing -> "RELEASING"
            StatusManga.NotYetReleased -> "NOT_YET_RELEASED"
            StatusManga.Cancelled -> "CANCELLED"
            is StatusManga.Unknown -> value.rawValue
        }
        encoder.encodeString(string)
    }
}