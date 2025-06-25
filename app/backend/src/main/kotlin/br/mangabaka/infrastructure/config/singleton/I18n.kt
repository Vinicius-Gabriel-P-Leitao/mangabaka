/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.config.singleton

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.throwable.base.InternalException
import kotlinx.serialization.MissingFieldException
import java.text.MessageFormat
import java.util.*

object I18n {
    private val locale: Locale = run {
        val env = System.getenv("APP_LOCALE") ?: "pt-BR"
        Locale.forLanguageTag(env.replace('_', '-'))
    }

    private val bundle: ResourceBundle = ResourceBundle.getBundle("messages", locale)

    fun get(key: String, vararg args: Any): String {
        return try {
            val pattern = bundle.getString(key)
            MessageFormat(pattern).format(args)
        } catch (exception: MissingResourceException) {
            throw InternalException(
                message = InternalErrorCode.ERROR_TRANSLATE.handle("Erro ao encontrar chave de tradução: $key"),
                errorCode = InternalErrorCode.ERROR_TRANSLATE
            )
        }
    }
}