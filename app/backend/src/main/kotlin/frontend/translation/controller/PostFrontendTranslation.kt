/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package frontend.translation.controller

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/frontend/translation")
class PostFrontendTranslation {
    @GET
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAnilistMetadataManga(@QueryParam("name") nameManga: String?): Response {
        return try {
            Response.ok("result.paginationInfo.page").build()
        } catch (exception: Exception) {
            Response.status(500).entity("result.paginationInfo.page").build()
        }
    }
}