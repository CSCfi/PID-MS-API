package fi.csc.pid.api.handle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static fi.csc.pid.api.doi.DOI.uriFromString;

import static fi.csc.pid.api.handle.TLS.getSslParam;

public class Handle {

    public static final String SURF_SERVICE_PREFIX = "11113/lb-";
    public static final String GWDG_SERVICE_PREFIX = "21.T13999/EOSC-";
    public static final String EPIC_PROTOCOL = "https://";

    //Jag förstår inte, men fungerar
     static final String JSON1 = """
            {"values": [
               {"index":1,
                "type":"URL",
                "data":{"format":"string","value":""";

    public static final String JSONSURF = """
                    }},
               {"index":100,
                "type":"HS_ADMIN",
                "data":{"format":"admin","value":{"handle":"0.NA/11113","index":200,"permissions":"011111110011"}}}
               ]}
            """;
    public static final String JSONGWDG = """
                 }},
               {"index":100,
                "type":"HS_ADMIN",
                "data":
        {
         "format":"admin",
         "value":{"handle":"0.NA/21.T13999","index":300,"permissions":"110011111110"}
        }
    }
    ]}
    """;

    static final String HIPSU = "\"";

    // Konfiguraatio tiedostosta resources/application.properties
    String host;
    String port;
    String api; //  /api/handles/
    String user;
    private final byte[]  privateData;
    TLS tls;

    public Handle(String host, String port,String api, String user, String key, String kspass ) {
        this.host = host;
        this.port = port;
        this.api = api;
        this.user = user;
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "");
        //System.out.println("Debug avain:"+privateKeyPEM);
        privateData = Base64.getDecoder().decode(privateKeyPEM);
        this.tls = new TLS(kspass);
    }


    public static String json(String url, String loppu) {

        return JSON1 + HIPSU + url + HIPSU + loppu;
    }

    public boolean create(String url, String prefix, String suffix, String loppu) {
        String json = json(url, loppu);
        URI uri = uriFromString(EPIC_PROTOCOL+host+":"+port+api + prefix + suffix);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Handle clientCert=\"true\"")
                .header("Content-Lengt", String.valueOf(json.length()))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try (HttpClient client = HttpClient.newBuilder()
                .sslContext(tls.getSSLContext(host, user, privateData))
                .sslParameters(getSslParam())
                .build()) {
            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.statusCode() + response.body());
                return true;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //return false;
    }


}
