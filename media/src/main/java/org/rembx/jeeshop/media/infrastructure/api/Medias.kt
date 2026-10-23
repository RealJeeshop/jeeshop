package org.rembx.jeeshop.media.infrastructure.api

import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.constraints.NotNull
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput
import org.rembx.jeeshop.media.domain.MediaStorage
import org.rembx.jeeshop.rest.WebApplicationException
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN
import org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY
import org.rembx.jeeshop.role.JeeshopRoles.STORE_ADMIN
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.InputStream

@Path("/medias")
@ApplicationScoped
class Medias(private val mediaStorage: MediaStorage) {
    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN, STORE_ADMIN)
    @Path("/{type}/{id}/{locale}/upload")
    fun upload(
        multipart: MultipartFormDataInput,
        @NotNull @PathParam("type") itemType: String,
        @NotNull @PathParam("id") itemId: Long,
        @NotNull @PathParam("locale") locale: String,
    ) {
        try {
            for (parts in multipart.formDataMap.values) {
                for (part in parts) {
                    val fileName = part.fileName ?: continue
                    part.getBody(InputStream::class.java, null).use { stream ->
                        mediaStorage.store(itemType, itemId, locale, fileName, stream)
                    }
                }
            }
        } catch (e: IOException) {
            LOG.error("Could not handle upload of file with type: {} and id: {}", itemType, itemId, e)
            throw WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR)
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN, ADMIN_READONLY)
    @Path("/{type}/{id}/{locale}/{filename}")
    fun get(
        @NotNull @PathParam("type") itemType: String,
        @NotNull @PathParam("id") itemId: Long,
        @NotNull @PathParam("locale") locale: String,
        @NotNull @PathParam("filename") fileName: String,
    ): File = mediaStorage.find(itemType, itemId, locale, fileName)
        ?: throw WebApplicationException(Response.Status.NOT_FOUND)

    companion object {
        private val LOG = LoggerFactory.getLogger(Medias::class.java)
    }
}
