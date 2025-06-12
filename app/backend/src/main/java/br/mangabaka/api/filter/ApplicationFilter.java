package br.mangabaka.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ApplicationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();

        boolean isApi = uri.startsWith("/v1");
        boolean isStatic = uri.matches(".*\\.(js|css|png|jpg|svg|ico|json|woff2?)$");

        if (!isApi && !isStatic && !uri.equals("/") && !uri.contains(".")) {
            request.getRequestDispatcher("/index.html").forward(request, response);
        } else {
            chain.doFilter(req, res);
        }
    }
}