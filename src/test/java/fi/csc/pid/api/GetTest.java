package fi.csc.pid.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class GetTest {
    private static final String PROPERTIES = "../../tyo/sala/apikey.properties";
    static String apikey;

    /**
     * Huomaa, että URL on encodattu
     */
    @Test
    public void testGetPID() {
        given().header(new Header("apikey", apikey))
          .when().get("/get/v1/pid/?URL=https%3A%2F%2Fwww.kielipankki.fi%2Ftyokalut%2F")
               .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("pid", equalTo("urn:nbn:fi:lb-2014120219"));
    }

    /**
     * Huomaa että URN ei ole encodattu, mennään niin kauan ilman kun toimii.
     */
    @Test
    public void testGetURL() {
        given().header(new Header("apikey", apikey))
          .when().get("/get/v1/pid/urn:nbn:fi:lb-2014120219")
               .then()
                .statusCode(200)
                .contentType(MediaType.TEXT_PLAIN)
                .body(is("https://www.kielipankki.fi/tyokalut/"));
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
