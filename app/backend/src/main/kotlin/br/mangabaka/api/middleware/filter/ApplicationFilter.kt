/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.middleware.filter

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