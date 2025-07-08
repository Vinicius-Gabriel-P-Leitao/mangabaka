/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.service

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.code.custom.SqlErrorCode
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.exception.throwable.http.SqlException
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.model.FrontendTranslation
import frontend.translation.repository.FrontendTranslationRepo
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.postgresql.util.PSQLState
import java.sql.SQLException
import kotlin.test.assertEquals

class SaveFrontendTranslationServiceTest {

    private val repositoryMock: FrontendTranslationRepo = mock()

    private val service = SaveFrontendTranslationService(
        repository = repositoryMock,
    )

    val validJsonTranslation: String = """
        {
            "meta": {
                "code": "pt_BR",
                "label": "Português (Brasil)"
            },
            "page": {
                "notFound": {
                    "title": "Página não encontrada 😢",
                    "message": "Não foi possível encontrar:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Frieren do anime Sousou no Frieren."
                },
                "internalServerError": {
                    "title": "Erro interno 💀",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "De Sousou no Frieren: {param}"
                },
                "gatewayTimeout": {
                    "title": "Tempo limite do Gateway 🧐",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Himmel do anime Sousou no Frieren."
                },
                "badRequest": {
                    "title": "Request mal formado 🤨",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "De Sousou no Frieren: {param}"
                },
                "badGateway": {
                    "title": "Erro de Gateway 🧐",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Flame do anime Sousou no Frieren."
                },
                "conflict": {
                    "title": "Conflito de dados 🤯",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Ubel do anime Sousou no Frieren."
                },
                "forbidden": {
                    "title": "Conexão proibida 🥹",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Heiter do anime Sousou no Frieren."
                },
                "unavailable": {
                    "title": "Serviço não disponível 🤐",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Sense do anime Sousou no Frieren."
                },
                "methodNotAllowed": {
                    "title": "Método não permitido 😬",
                    "message": "Não foi possível em:",
                    "cause": "Motivo:",
                    "imageAlt": "Personagem Methode do anime Sousou no Frieren."
                },  
                "home": {
                    "title": "Página principal"
                }
            },
            "component": {
                "translation": {
                    "infoView": "Campo para trocar idioma da interface."
                },
                "select": {
                    "label": "Language:"
                }
            },
            "handler": {
                "unknown": {
                    "unknown": "Erro desconhecido. {param}",
                    "unexpectedError": "Ocorreu um erro inesperado: {param}",
                    "unidentifiedError": "Algo deu errado, mas não conseguimos identificar o erro."
                },
                "notFound": {
                    "resource": "Recurso solicitado",
                    "couldNotFind": "Não foi possível encontrar: {param}"
                },
                "badRequest": {
                    "invalidField": "Campo inválido: {param}",
                    "malformedRequest": "A requisição está mal formada. Verifique os dados enviados."
                },
                "badGateway": {
                    "invalidGateway": "Gateway inválido: o servidor intermediário falhou.",
                    "gatewayTimeout": "Tempo de resposta excedido do servidor de origem.",
                    "intermediaryServer": "Erro ao acessar o servidor intermediário. Código: {param}"
                },
                "gatewayTimeout": {
                    "tryAgainLater": "O servidor demorou demais para responder. Tente novamente mais tarde."
                },
                "invalidData": {
                    "obtainedInvalid": "Ao buscar dados em {url}, foram considerados inválidos. {param}"
                }
            }
        }
    """

    val json = Json { ignoreUnknownKeys = true }
    val validI18nJsonFormat: I18nJsonFormat = json.decodeFromString(string = validJsonTranslation)

    @Test
    fun `should save new translations successful response`() {
        whenever(methodCall = repositoryMock.findByKeyAndLang(key = any(), lang = any())).thenReturn(null)
        whenever(methodCall = repositoryMock.save(entity = any())).thenAnswer { it.arguments[0] as FrontendTranslation }

        val result = service.saveTranslation(data = validJsonTranslation)

        verify(mock = repositoryMock, mode = atLeastOnce()).save(entity = any())
        assertEquals(expected = "Português (Brasil)", actual = result.meta.label)
    }

    @Test
    fun `should throw exception when meta-language is blank`() {
        val invalidJsonMetaLanguageIsBlank = """
        {
            "meta": { "language": "" },
            "page": {}
        }
        """.trimIndent()

        val ex = assertThrows<MetadataException> {
            service.saveTranslation(invalidJsonMetaLanguageIsBlank)
        }

        assertEquals(expected = MetadataErrorCode.ERROR_JSON_MALFORMED, actual = ex.errorCode)
    }

    @Test
    fun `should throw exception when json is invalid serialization`() {
        val invalidJsonDataIsBlank = """
        {
            "meta": { "language": "en-us" },
            "page": {}
        }
        """.trimIndent()

        val exception = assertThrows<MetadataException> {
            service.saveTranslation(data = invalidJsonDataIsBlank)
        }

        assertEquals(expected = MetadataErrorCode.ERROR_JSON_MALFORMED, actual = exception.errorCode)
    }

    @Test
    fun `saveTranslation should throw SqlException when callbackSave is empty`() {
        whenever(methodCall = repositoryMock.findByKeyAndLang(key = any(), lang = any())).thenReturn(null)
        whenever(methodCall = repositoryMock.save(entity = any())).thenReturn(null)

        val exception = assertThrows<MetadataException> {
            service.saveTranslation(data = validJsonTranslation)
        }

        assertEquals(expected = MetadataErrorCode.ERROR_EMPTY_FIELD, actual = exception.errorCode)
    }

    @Test
    fun `saveTranslation should throw InternalException is not present SQLException-state`() {
        whenever(methodCall = repositoryMock.findByKeyAndLang(key = any(), lang = any())).thenReturn(null)
        whenever(methodCall = repositoryMock.save(entity = any())).thenThrow(
            RuntimeException(
                "Is invalid SQLException",
                SQLException("Is invalid SQLException")
            )
        )

        val exception = assertThrows<InternalException> {
            service.saveTranslation(data = validJsonTranslation)
        }

        assertEquals(expected = InternalErrorCode.ERROR_INTERNAL_SQL, actual = exception.errorCode)
    }

    @Test
    fun `saveTranslation should throw InternalException is not SQLException`() {
        whenever(methodCall = repositoryMock.findByKeyAndLang(key = any(), lang = any())).thenReturn(null)
        whenever(methodCall = repositoryMock.save(entity = any())).thenThrow(
            RuntimeException("Is not SQLException")
        )

        val exception = assertThrows<InternalException> {
            service.saveTranslation(data = validJsonTranslation)
        }

        assertEquals(expected = InternalErrorCode.ERROR_INTERNAL_GENERIC, actual = exception.errorCode)
    }

    @Test
    fun `saveTranslation should throw SqlException when erro code PSQLState-state`() {
        whenever(methodCall = repositoryMock.findByKeyAndLang(key = any(), lang = any())).thenReturn(null)

        val listPSQLStateCode = listOf(
            PSQLState.UNIQUE_VIOLATION,
            PSQLState.FOREIGN_KEY_VIOLATION,
            PSQLState.NOT_NULL_VIOLATION,
            PSQLState.STRING_DATA_RIGHT_TRUNCATION,
            PSQLState.DEADLOCK_DETECTED,
            PSQLState.SYNTAX_ERROR,
            PSQLState.UNDEFINED_COLUMN,
            PSQLState.CONNECTION_UNABLE_TO_CONNECT,
        )

        val expectedErrorCodes = listOf(
            SqlErrorCode.ERROR_DUPLICATE_CONSTRAINT,
            SqlErrorCode.ERROR_FOREIGN_KEY_CONSTRAINT,
            SqlErrorCode.ERROR_NOT_NULL_VIOLATION,
            SqlErrorCode.ERROR_DATA_TOO_LONG,
            SqlErrorCode.ERROR_DEADLOCK_DETECTED,
            SqlErrorCode.ERROR_SYNTAX,
            SqlErrorCode.ERROR_UNDEFINED_COLUMN,
            SqlErrorCode.ERROR_CONNECTION_FAILURE,
        )

        listPSQLStateCode.zip(other = expectedErrorCodes).forEach { (psqlState, expectedErrorCode) ->
            whenever(methodCall = repositoryMock.findByKeyAndLang(key = any(), lang = any())).thenReturn(null)
            whenever(methodCall = repositoryMock.save(entity = any())).thenThrow(
                RuntimeException(
                    psqlState.name,
                    SQLException(psqlState.name, psqlState.state)
                )
            )

            val exception = assertThrows<SqlException> {
                service.saveTranslation(data = validJsonTranslation)
            }

            assertEquals(expectedErrorCode, exception.errorCode)
        }
    }
}
