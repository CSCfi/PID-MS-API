package fi.csc.pid.api.handle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static fi.csc.pid.api.doi.DOI.uriFromString;
import static fi.csc.pid.api.handle.TLS.getSSLContext;
import static fi.csc.pid.api.handle.TLS.getSslParam;

public class Handle {

    public static final String EPIC_SERVICE_PREFIX = "11113/lb-"; // Toistaiseksi vain tällä prefixilla!
    //uuden asiakkaan on neuvoteltava lb- tilalle oma avauus eudatin kanssa tai kokonaan uusi prefix
    public static final String EPIC_PROTOCOL = "https://";

    //Jag förstår inte, men fungerar
     static final String JSON1 = """
            {"values": [
               {"index":1,
                "type":"URL",
                "data":{"format":"string","value":""";

    static final String JSON2 = """
                    }},
               {"index":100,
                "type":"HS_ADMIN",
                "data":{"format":"admin","value":{"handle":"0.NA/11113","index":200,"permissions":"011111110011"}}}
               ]}
            """;

    static final String HIPSU = "\"";

    // Konfiguraatio tiedostosta resources/application.properties
    String host; //epic-pid.storage.surfsara.nl
    String port; //8004
    String api; //  /api/handles/
    String user; //USER01_311_11113
    private final byte[]  privateData;

    public Handle(String host, String port,String api, String epickey ) {
        this.host = host;
        this.port = port;
        this.api = api;
        String privateKeyPEM = epickey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "");
        //System.out.println("Debug avain:"+privateKeyPEM);
        privateData = Base64.getDecoder().decode(privateKeyPEM);
    }


    public static String json(String url) {
        return JSON1 + HIPSU + url + HIPSU + JSON2;
    }

    public boolean create(String url, String suffix) {
        String json = json(url);
        URI uri = uriFromString(EPIC_PROTOCOL+host+":"+port+api+EPIC_SERVICE_PREFIX + suffix);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Handle clientCert=\"true\"")
                .header("Content-Lengt", String.valueOf(json.length()))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpClient client = HttpClient.newBuilder()
                .sslContext(getSSLContext(host, user, privateData))
                .sslParameters(getSslParam())
                .build();
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
        //return false;
    }


}
