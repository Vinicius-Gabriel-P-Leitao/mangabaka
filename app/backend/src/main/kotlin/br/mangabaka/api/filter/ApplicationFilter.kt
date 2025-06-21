package br.mangabaka.api.filter

import jakarta.servlet.*
import jakarta.servlet.annotation.WebFilter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException

@WebFilter("/*")
class ApplicationFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest?, res: ServletResponse?, chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse?

        val uri = request.requestURI

        val isApi = uri.startsWith("/v1")
        val isStatic = uri.matches(".*\\.(js|css|png|jpg|svg|ico|json|woff2?)$".toRegex())

        if (!isApi && !isStatic && (uri != "/") && !uri.contains(".")) {
            request.getRequestDispatcher("/index.html").forward(request, response)
        } else {
            chain.doFilter(req, res)
        }
    }
}