package fi.csc.pid.api.handle;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;

public class TLS {

    public static final String KEYSTORE_PASSWORD = System.getenv("KEYSTORESS"); //nonse

    public static SSLContext getSSLContext(String host, String user, byte[] privateData) {
        final CertificateFactory certificateFactory;
        final char[] pwdChars = KEYSTORE_PASSWORD.toCharArray();
        KeyManagerFactory keyMgrFactory = null;
        try {
            certificateFactory  = CertificateFactory.getInstance("X.509");
            final Collection<? extends Certificate> chain = certificateFactory.generateCertificates(
                    new ByteArrayInputStream(user.getBytes(StandardCharsets.UTF_8)));
            final Key key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateData));
            KeyStore clientKeyStore = KeyStore.getInstance("jks");
            clientKeyStore.load(null, null);
            clientKeyStore.setKeyEntry(host, key, pwdChars, chain.toArray(new Certificate[0]));
            keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
            keyMgrFactory.init(clientKeyStore, pwdChars);
        } catch (CertificateException | KeyStoreException e) {
            System.err.println(e.getMessage());
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
        try {
            SSLContext sslCtx = SSLContext.getInstance("TLSv1.3");
            sslCtx.init(keyMgrFactory.getKeyManagers(), null, null);
            return sslCtx;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLParameters getSslParam() {
        //repetitio mare studiorum est
        SSLParameters sslParam = new SSLParameters();
        sslParam.setNeedClientAuth(true);
        return sslParam;
    }

}
