package fi.csc.pid.api;

import fi.csc.pid.api.model.DoiJson;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DoiTest {
    static final int SD = 5;

    @Test
    public void doiJson() {
        String kelvoton = "{}";
        DoiJson kdj = new DoiJson(kelvoton, 0, SD);
        System.out.println(kdj.errorResponse.toString());
        assert kdj.error : "Tämän piti ollakin kelvoton";

        String kelvollinen = """
                { "doi": { "data": { "attributes": { "url": "https://www.asdf1.com"}}}}""";
        DoiJson dj = new DoiJson(kelvollinen, 0, SD);
         assert dj.error : "Tämän piti ollakin kelvollinen";

       String draft = """
  {
  "data": {
    "type": "dois",
    "attributes": {
        "doi": ""
    }
  }
}
""";
    DoiJson ddj =   new DoiJson(draft, 5, SD);
    assert dj.error : "Ok";
    }
}
