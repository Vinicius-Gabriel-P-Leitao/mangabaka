package br.mangabaka;


import br.mangabaka.controller.Test;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

    public class Main {
        public static void main(String[] args) {
            Server server = new Server();
            ServerConnector connector = new ServerConnector(server);
            ServletContextHandler servletContextHandler = new ServletContextHandler();

            connector.setHost("localhost");
            connector.setPort(8080);

            server.addConnector(connector);

            servletContextHandler.setContextPath("/v1/");
            servletContextHandler.addServlet(new ServletHolder(new Test()), "/hello ");

            server.setHandler(servletContextHandler);

            try {
                server.start();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }