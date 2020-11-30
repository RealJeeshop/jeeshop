package org.rembx.jeeshop.user.tools;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CryptToolsTest {

    @Test
    public void testHashSha256Base64() throws Exception {
        assertThat(CryptTools.hashSha256Base64("jeeshop")).isEqualTo("DjYu7nlNFk6BdxO+LwxZJ3mBAfxgwytTS2cVRbmnIO8=");
    }
}