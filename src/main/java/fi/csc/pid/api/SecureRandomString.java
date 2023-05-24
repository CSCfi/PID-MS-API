package fi.csc.pid.api;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureRandomString {

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public String generate(int bytes) {
        byte[] buffer = new byte[bytes];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }
}

