/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.exception.code.custom;

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.infrastructure.config.singleton.I18n
import org.postgresql.util.PSQLState

enum class SqlErrorCode : ErrorCodeProvider {
    ERROR_PERSIST_DATA {
        override fun handle(value: String) = I18n.get("error.code.sql.persisting.data", value)
    },

    ERROR_DUPLICATE_CONSTRAINT {
        override fun handle(value: String) = I18n.get("error.code.sql.duplicate.constraint", value)
    },

    ERROR_FOREIGN_KEY_CONSTRAINT {
        override fun handle(value: String) = I18n.get("error.code.sql.foreign.key", value)
    },

    ERROR_NOT_NULL_VIOLATION {
        override fun handle(value: String) = I18n.get("error.code.sql.not.null", value)
    },

    ERROR_DATA_TOO_LONG {
        override fun handle(value: String) = I18n.get("error.code.sql.data.too.long", value)
    },

    ERROR_DEADLOCK_DETECTED {
        override fun handle(value: String) = I18n.get("error.code.sql.deadlock", value)
    },

    ERROR_SYNTAX {
        override fun handle(value: String) = I18n.get("error.code.sql.syntax", value)
    },

    ERROR_UNDEFINED_COLUMN {
        override fun handle(value: String) = I18n.get("error.code.sql.undefined.column", value)
    },

    ERROR_PERMISSION_DENIED {
        override fun handle(value: String) = I18n.get("error.code.sql.permission.denied", value)
    },

    ERROR_CONNECTION_FAILURE {
        override fun handle(value: String) = I18n.get("error.code.sql.connection.failure", value)
    };

    companion object {
        fun mapSqlStateToErrorCode(sqlState: String): SqlErrorCode? = when (sqlState) {
            PSQLState.UNIQUE_VIOLATION.state -> ERROR_DUPLICATE_CONSTRAINT
            PSQLState.FOREIGN_KEY_VIOLATION.state -> ERROR_FOREIGN_KEY_CONSTRAINT
            PSQLState.NOT_NULL_VIOLATION.state -> ERROR_NOT_NULL_VIOLATION
            PSQLState.STRING_DATA_RIGHT_TRUNCATION.state -> ERROR_DATA_TOO_LONG
            PSQLState.DEADLOCK_DETECTED.state -> ERROR_DEADLOCK_DETECTED
            PSQLState.SYNTAX_ERROR.state -> ERROR_SYNTAX
            PSQLState.UNDEFINED_COLUMN.state -> ERROR_UNDEFINED_COLUMN
            PSQLState.CONNECTION_UNABLE_TO_CONNECT.state -> ERROR_CONNECTION_FAILURE
            else -> null
        }
    }
}