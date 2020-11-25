package org.rembx.jeeshop.admin;

import org.apache.commons.io.IOUtils;
import org.rembx.jeeshop.catalog.*;
import org.rembx.jeeshop.media.Medias;
import org.rembx.jeeshop.order.Orders;
import org.rembx.jeeshop.rest.WebApplicationExceptionMapper;
import org.rembx.jeeshop.user.MailTemplates;
import org.rembx.jeeshop.user.Users;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Path("/")
public class GatewayResource {

    private static final String FALLBACK_RESOURCE = "/webapp/index.html";

    @GET
    @Path("/")
    public Response getFrontendRoot() throws IOException {
        return getFrontendStaticFile("index.html");
    }


    @GET
    @Path("/{fileName:.+}")
    public Response getFrontendStaticFile(@PathParam("fileName") String fileName) {
        final InputStream requestedFileStream = GatewayResource.class.getResourceAsStream("/webapp/" + fileName);
        final InputStream inputStream = requestedFileStream != null ?
                requestedFileStream :
                GatewayResource.class.getResourceAsStream(FALLBACK_RESOURCE);
        final StreamingOutput streamingOutput = outputStream -> IOUtils.copy(inputStream, outputStream);
        return Response
                .ok(streamingOutput)
                .cacheControl(CacheControl.valueOf("max-age=900"))
                .type(getMimeType(fileName, inputStream))
                .build();
    }

    private String getMimeType(String filename, InputStream inputStream) {
        String type = null;
        try {
            type = URLConnection.guessContentTypeFromStream(inputStream);
            if (type == null) {

                if (filename.contains("css")) return "text/css";
                else if (filename.contains("js")) return "text/javascript";
                else return "tex/html";

            } else {
                return type;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "tex/html";
        }
    }

}