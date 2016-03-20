package org.rembx.jeeshop.media;

import org.junit.Before;
import org.junit.Test;
import org.rembx.jeeshop.rest.WebApplicationException;

import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class MediasTest {

    Medias medias;

    @Before
    public void setup() {
        medias = new Medias();
    }

    @Test
    public void get_shouldReturnFile_whenThereIsAFileWithPathMatchingGivenParams() throws Exception {

        java.nio.file.Path basePath = medias.getBasePath();

        java.nio.file.Path testFilePath = basePath.resolve("categories").resolve("999").resolve("en_GB");

        if (!Files.exists(testFilePath)) {
            Files.createDirectories(testFilePath);
        }

        File testFile = new File(testFilePath.toFile(), "testMedias.test");
        testFile.createNewFile();

        assertThat(medias.get("categories", 999L, "en_GB", "testMedias.test")).isNotNull();

    }

    @Test
    public void get_shouldThrowNotFound_whenThereAreNoFileMatchingGivenParams() throws Exception {
        try {
            medias.get("categories", 999L, "en_GB", "unknown.test");
            fail("Should have thrown ex");
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

}