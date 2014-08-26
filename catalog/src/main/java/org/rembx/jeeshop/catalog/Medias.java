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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Medias resource
 */
@Path("/medias")
public class Medias {

    private final static String SERVER_UPLOAD_LOCATION_FOLDER = "jeeshop-media";
    private static Logger LOG = LoggerFactory.getLogger(Medias.class);

    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(JeeshopRoles.ADMIN)
    @Path("/{type}/{id}/upload")
    public void uploadFile(@Context HttpServletRequest request, @NotNull @PathParam("type") String itemType, @NotNull @PathParam("id") Long itemId) {

        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                java.nio.file.Path itemBasePath = Paths.get(SERVER_UPLOAD_LOCATION_FOLDER).resolve(itemType).resolve(itemId.toString());
                if (!Files.exists(itemBasePath))
                    Files.createDirectories(itemBasePath);
                Files.copy(item.openStream(), itemBasePath.resolve(item.getName()), StandardCopyOption.REPLACE_EXISTING);
            }


        } catch (IOException | FileUploadException e) {
            LOG.error("Could not handle upload of file with type: " + itemType + " and id: " + itemId, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

}

