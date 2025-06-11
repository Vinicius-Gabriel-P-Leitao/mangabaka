package br.mangabaka.controller;

import io.ebean.DatabaseFactory;
import io.ebean.annotation.Platform;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/example")
public class ExampleResource {
    private final Dotenv dotenv = Dotenv.load();
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExample() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();

        dataSourceConfig.setPassword(dotenv.get("PG_PASSWORD"));
        dataSourceConfig.setUsername(dotenv.get("PG_USERNAME"));
        dataSourceConfig.setUrl(dotenv.get("PG_JDBC_URL"));
        dataSourceConfig.setSchema("mangabaka");
        dataSourceConfig.setPlatform(Platform.POSTGRES.name());

        DatabaseConfig databaseConfig = new DatabaseConfig();

        databaseConfig.setDataSourceConfig(dataSourceConfig);
        databaseConfig.setDdlGenerate(true);
        databaseConfig.setDdlRun(true);
        databaseConfig.setDbSchema("mangabaka");

        DatabaseFactory.create(databaseConfig);

        var data = new ExampleData("Hello", "World");
        return Response.ok(data).build();
    }
}

record ExampleData(String key, String value) {
}