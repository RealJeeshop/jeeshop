package org.rembx.jeeshop.catalog;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.rembx.jeeshop.role.JeeshopRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Medias resource
 */
@Path("/medias")
/**
 * TODO file location base folder to be accessible by web server
 */
public class Medias {

    final static String SERVER_UPLOAD_LOCATION_FOLDER = "jeeshop-media";
    private static Logger LOG = LoggerFactory.getLogger(Medias.class);

    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{type}/{id}/{locale}/upload")
    public void upload(@Context HttpServletRequest request, @NotNull @PathParam("type") String itemType, @NotNull @PathParam("id") Long itemId, @NotNull @PathParam("locale") String locale) {

        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                java.nio.file.Path itemBasePath = Paths.get(SERVER_UPLOAD_LOCATION_FOLDER).resolve(itemType).resolve(itemId.toString()).resolve(locale);
                if (!Files.exists(itemBasePath))
                    Files.createDirectories(itemBasePath);
                Files.copy(item.openStream(), itemBasePath.resolve(item.getName()), StandardCopyOption.REPLACE_EXISTING);
            }


        } catch (IOException | FileUploadException e) {
            LOG.error("Could not handle upload of file with type: " + itemType + " and id: " + itemId, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{type}/{id}/{locale}/{filename}")
    public File get(@NotNull @PathParam("type") String itemType, @NotNull @PathParam("id") Long itemId,
                        @NotNull @PathParam("locale") String locale, @NotNull @PathParam("filename") String fileName) {
        java.nio.file.Path filePath = Paths.get(SERVER_UPLOAD_LOCATION_FOLDER).resolve(itemType).resolve(itemId.toString()).resolve(locale).resolve(fileName);
        if (!Files.exists(filePath))
            throw new WebApplicationException(Response.Status.NOT_FOUND);

        return filePath.toFile();

    }

    /*
    @DELETE
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{type}/{id}/{locale}/{filename}")
    public void delete(@Context HttpServletRequest request, @NotNull @PathParam("type") String itemType, @NotNull @PathParam("id") Long itemId,
                        @NotNull @PathParam("locale") String locale, @NotNull @PathParam("filename") String fileName) {
        java.nio.file.Path filePath = Paths.get(SERVER_UPLOAD_LOCATION_FOLDER).resolve(itemType).resolve(itemId.toString()).resolve(locale).resolve(fileName);
        if (Files.exists(filePath))
            filePath.toFile().delete();
    }
    */
}

