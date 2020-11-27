package org.rembx.jeeshop.admin;

import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Path("/")
public class GatewayResource {

    private static final String FALLBACK_RESOURCE = "/webapp/index.html";

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
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
                else if (filename.contains("jpg")) return "img/jpeg";
                else if (filename.contains("jpeg")) return "img/jpeg";
                else return "text/html";

            } else {
                return type;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "text/html";
        }
    }

}