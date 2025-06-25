/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.config.database

import io.ebean.DatabaseFactory
import io.ebean.annotation.Platform
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig

class PostgresqlConfig {

    fun configure() {
        val dataSourceConfig = DataSourceConfig()

        dataSourceConfig.setPassword(System.getenv("PG_PASSWORD"))
        dataSourceConfig.setUsername(System.getenv("PG_USERNAME"))
        dataSourceConfig.setUrl(System.getenv("PG_JDBC_URL"))
        dataSourceConfig.setSchema("mangabaka")
        dataSourceConfig.setPlatform(Platform.POSTGRES.name)

        val databaseConfig = DatabaseConfig()

        databaseConfig.setDataSourceConfig(dataSourceConfig)
        databaseConfig.setDdlGenerate(true)
        databaseConfig.setDdlRun(true)
        databaseConfig.setDbSchema("mangabaka")

        DatabaseFactory.create(databaseConfig)
    }
}