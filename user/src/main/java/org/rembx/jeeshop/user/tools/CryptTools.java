package org.rembx.jeeshop.user.tools;

import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Base64;

public class CryptTools {

    public static String hashSha256Base64(String strToHash) {
        byte[] digest = Hashing.sha256().hashBytes(strToHash.getBytes()).asBytes();
        return Base64.encodeBase64String(digest);
    }
}