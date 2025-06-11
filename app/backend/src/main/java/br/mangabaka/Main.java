package br.mangabaka;

import io.ebean.DatabaseFactory;
import io.ebean.annotation.Platform;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/v1/"})
public class Main extends HttpServlet {
    private final Dotenv dotenv = Dotenv.load();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
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

        resp.setContentType("application/json");
        resp.getWriter().write("{ \"key\": \"Servidor jetty 12.0.22 rodando!\" }");
    }
}
