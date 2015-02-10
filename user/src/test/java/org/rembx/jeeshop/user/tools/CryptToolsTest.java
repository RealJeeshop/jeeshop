package org.rembx.jeeshop.user.tools;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class CryptToolsTest {

    @Test
    public void testHashSha256Base64() throws Exception {
        assertThat(CryptTools.hashSha256Base64("jeeshop")).isEqualTo("DjYu7nlNFk6BdxO+LwxZJ3mBAfxgwytTS2cVRbmnIO8=");
    }
}