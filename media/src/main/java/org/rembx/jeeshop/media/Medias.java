package org.rembx.jeeshop.media;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.rembx.jeeshop.rest.WebApplicationException;
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

import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN;
import static org.rembx.jeeshop.role.JeeshopRoles.ADMIN_READONLY;

/**
 * TODO file location base folder to be accessible by web server
 */
@Path("/medias")
public class Medias {

    static final String JEESHOP_MEDIA_DIR = "jeeshop-media";
    static final String JEESHOP_DATA_DIR = "JEESHOP_DATA_DIR";
    static final String OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";
    private static Logger LOG = LoggerFactory.getLogger(Medias.class);

    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    @Path("/{type}/{id}/{locale}/upload")
    public void upload(@Context HttpServletRequest request,
                       @NotNull @PathParam("type") String itemType,
                       @NotNull @PathParam("id") Long itemId,
                       @NotNull @PathParam("locale") String locale) {

        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                java.nio.file.Path itemBasePath = getBasePath().resolve(itemType).resolve(itemId.toString()).resolve(locale);
                if (!Files.exists(itemBasePath))
                    Files.createDirectories(itemBasePath);
                java.nio.file.Path filePath = itemBasePath.resolve(item.getName());
                Files.copy(item.openStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                LOG.info("File written to " + filePath);
            }


        } catch (IOException | FileUploadException e) {
            LOG.error("Could not handle upload of file with type: " + itemType + " and id: " + itemId, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @RolesAllowed({ADMIN, ADMIN_READONLY})
    @Path("/{type}/{id}/{locale}/{filename}")
    public File get(@NotNull @PathParam("type") String itemType, @NotNull @PathParam("id") Long itemId,
                    @NotNull @PathParam("locale") String locale, @NotNull @PathParam("filename") String fileName) {
        java.nio.file.Path filePath = getBasePath().resolve(itemType).resolve(itemId.toString()).resolve(locale).resolve(fileName);
        if (!Files.exists(filePath))
            throw new WebApplicationException(Response.Status.NOT_FOUND);

        return filePath.toFile();

    }

    java.nio.file.Path getBasePath() {
        java.nio.file.Path path;

        if (StringUtils.isNotEmpty(System.getenv(OPENSHIFT_DATA_DIR))) {
            path = Paths.get(System.getenv(OPENSHIFT_DATA_DIR)).resolve(JEESHOP_MEDIA_DIR);
        } else if (StringUtils.isNotEmpty(System.getenv(JEESHOP_DATA_DIR))) {
            path = Paths.get(System.getenv(JEESHOP_DATA_DIR)).resolve(JEESHOP_MEDIA_DIR);
        }else{
            path = Paths.get(Medias.JEESHOP_MEDIA_DIR);
        }

        return path;
    }
}

