package fi.csc.pid.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;
import static org.hamcrest.CoreMatchers.is;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import jakarta.ws.rs.core.MediaType;
import static io.restassured.RestAssured.given;
import io.restassured.http.Header;

@QuarkusTest
public class KopioiTest {
       private static final String PROPERTIES = "../../tyo/sala/apikey.properties";
    static String apikey;
    static final String  PID = "urn:nbn:fi:csc:test-20220614_1258";
    /**
     * Tämä testi oli kertakäyttöinen ja nyt ihan typerä kun tarkistetaan vain että 501
     * Mutta en poista koodia, jos tästä tulee joskus suuri järjestelmä muistissa olevalla
     * testitietokannalla
     */
    @Test
    public void testKopioi() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("URL", "https://www.pekkajarvelainen.fi/pj/");
        given().header(new Header("apikey", apikey))
                .header(new Header("Content-Type", "application/json"))
                .body(requestParams.toString())
                 .when().post("/v1/pid/"+PID)
                .then()
                .statusCode(501) //200
                /*.contentType(MediaType.TEXT_PLAIN)
                .body(is(pid))*/;
    }

    @Test
    /**
     * Tämäkin kertakäyttöinen 200 palauttavana, katso edellinen kommentti.
     */
    public void testUpdate() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("URL", "https://pj.dy.fi/pj/");
        given().header(new Header("apikey", apikey))
                .header(new Header("Content-Type", "application/json"))
                .body(requestParams.toString())
                 .when().post("/v1/pid/45166")
                .then()
                .statusCode(501); //200 toimi kerran
    }


        /**
     * Luetaan apikey properties tiedostosta
     * (oikeasti pitäisi lukea kannasta, mutta testataan APIa eikä kantaa)
     */
    static {
        Properties prop = new Properties();
        try {
         File f = new File(PROPERTIES);
         FileInputStream in = new FileInputStream(f);
         prop.load(in);
         apikey = prop.getProperty("apikey").trim();
         } catch (IOException ex) {
         ex.printStackTrace();
     }
    }
}
