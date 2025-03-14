package fi.csc.pid.api.handle;

import java.net.Socket;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
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
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;

public class TLS {
    String keysstorepass; //nonse

    public TLS(String keysstorepass) {
        this.keysstorepass = keysstorepass; //nonse
    }

    public  SSLContext getSSLContext(String host, String user, byte[] privateData) {
        final CertificateFactory certificateFactory;
        final char[] pwdChars = keysstorepass.toCharArray();
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
            if (host.equals("vm04.pid.gwdg.de")) {
                SSLContext sslCtx = SSLContext.getInstance("TLSv1.2");
                sslCtx.init(keyMgrFactory.getKeyManagers(),
                        new TrustManager[]
                                {
                                        new X509ExtendedTrustManager()
                                        {
                                            public X509Certificate[] getAcceptedIssuers()
                                            {
                                                return null;
                                            }

                                            public void checkClientTrusted(
                                                    final X509Certificate[] a_certificates,
                                                    final String a_auth_type) {
                                                //x509tm.checkClientTrusted(chain, authType);
                                            }

                                            public void checkServerTrusted(
                                                    final X509Certificate[] a_certificates,
                                                    final String a_auth_type)
                                            {
                                            }
                                            //@Override
                                            public void checkClientTrusted(
                                                    final X509Certificate[] a_certificates,
                                                    final String authType,
                                                    final Socket socket) {
                                                //x509tm.checkClientTrusted(chain, authType, socket);
                                            }
                                            public void checkServerTrusted(
                                                    final X509Certificate[] a_certificates,
                                                    final String a_auth_type,
                                                    final Socket a_socket)
                                            {
                                            }
                                            public void checkClientTrusted(
                                                    final X509Certificate[] a_certificates,
                                                    final String a_auth_type,
                                                    final SSLEngine a_engine)
                                            {
                                            }
                                            public void checkServerTrusted(
                                                    final X509Certificate[] a_certificates,
                                                    final String a_auth_type,
                                                    final SSLEngine a_engine)
                                            {
                                            }
                                        }
                                },
                        null);
                return sslCtx;
            } else {
                SSLContext sslCtx = SSLContext.getInstance("TLSv1.3");
                sslCtx.init(keyMgrFactory.getKeyManagers(), null, null);
                return sslCtx;
            }
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
