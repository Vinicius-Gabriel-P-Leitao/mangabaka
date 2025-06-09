package br.mangabaka.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Test extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("Servidor iniciado!");

        resp.setContentType("text/plain");
        resp.getWriter().write("Olá, Jetty está vivo!");
    }
}
