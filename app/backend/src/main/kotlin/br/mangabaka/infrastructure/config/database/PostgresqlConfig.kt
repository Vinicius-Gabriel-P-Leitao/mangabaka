/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.config.database

import io.ebean.DatabaseFactory
import io.ebean.annotation.Platform
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig
import io.github.cdimascio.dotenv.Dotenv

class PostgresqlConfig {
    private val dotenv: Dotenv = Dotenv.load()

    fun configure() {
        val dataSourceConfig = DataSourceConfig()

        dataSourceConfig.setPassword(dotenv.get("PG_PASSWORD"))
        dataSourceConfig.setUsername(dotenv.get("PG_USERNAME"))
        dataSourceConfig.setUrl(dotenv.get("PG_JDBC_URL"))
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