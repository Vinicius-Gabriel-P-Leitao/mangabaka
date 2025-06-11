package br.mangabaka.database.config;

import io.ebean.DatabaseFactory;
import io.ebean.annotation.Platform;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgresqlConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static void configure() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();

        dataSourceConfig.setPassword(dotenv.get("PG_PASSWORD"));
        dataSourceConfig.setUsername(dotenv.get("PG_USERNAME"));
        dataSourceConfig.setUrl(dotenv.get("PG_JDBC_URL"));
        dataSourceConfig.setSchema("mangabaka");
        dataSourceConfig.setPlatform(Platform.POSTGRES.name());

        io.ebean.config.DatabaseConfig databaseConfig = new DatabaseConfig();

        databaseConfig.setDataSourceConfig(dataSourceConfig);
        databaseConfig.setDdlGenerate(true);
        databaseConfig.setDdlRun(true);
        databaseConfig.setDbSchema("mangabaka");

        DatabaseFactory.create(databaseConfig);
    }
}
