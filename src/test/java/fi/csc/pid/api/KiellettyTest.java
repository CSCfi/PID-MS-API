package fi.csc.pid.api;

import io.quarkus.test.junit.QuarkusTest;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import io.restassured.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Täällä testatataan asioita, jotka eivät saa onnistua
 */
@QuarkusTest
public class KiellettyTest {

    private static final String PROPERTIES = "../../tyo/sala/apikey.properties";
    static String vääräkey;
    @Test
    /** Tarkistaa että asianmukaisista kohdista tulee 403
     *
     */
    public void testUpdate() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("URL", "https://pj.dy.fi/pj/");
        given().header(new Header("apikey", "jotainHumpuukkia"))
                .header(new Header("Content-Type", "application/json"))
                .body(requestParams.toString())
                 .when().post("/v1/pid/45166")
                .then()
                .statusCode(403); //Acces denied
    }

    @Test
    public void testUpdate2() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("URL", "https://pj.dy.fi/pj/");
        given().header(new Header("apikey", vääräkey))
                .header(new Header("Content-Type", "application/json"))
                .body(requestParams.toString())
                 .when().post("/v1/pid/45166")
                .then()
                .statusCode(501); // Huom! EI saa korjata toimimaan. Tässä testataan asioita, jotka eivät saa toimia!
    }

    static {
        Properties prop = new Properties();
        try {
            File f = new File(PROPERTIES);
            FileInputStream in = new FileInputStream(f);
            prop.load(in);
            vääräkey = prop.getProperty("vaarakey").trim();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
