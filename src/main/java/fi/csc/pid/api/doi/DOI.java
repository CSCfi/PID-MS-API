package fi.csc.pid.api.doi;

import fi.csc.pid.api.model.DoiJson;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class DOI {
    static final String CONTENTTYPE = "Content-Type";
    static final String JSON = "application/vnd.api+json";
    private final String address; // "https://api.test.datacite.org/dois";
    private final String datacite_salasana;
    private final DOIResource called;

    public DOI(DOIResource parent, String address, String datacite_salasana) {
        this.called = parent;
        this.address = address;
        this.datacite_salasana = datacite_salasana;
    }

    /**
     * Ceate new DOI
     *
     * @param doi String
     * @param doijson JSONObject {}
     * @param metadata JSONObject data
     * @param attributes JSONObject attributes metadata
     * @return boolean (+ possible error message)
     */
    public boolean create(String doi, JSONObject doijson, JSONObject metadata, JSONObject attributes) {
        //metadata.setDOI(doi);
        attributes.put("doi", doi);
        metadata.put("id", doi);
        metadata.put("attributes", attributes);
        doijson.put("data", metadata);
        URI uri = uriFromString(address);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header(CONTENTTYPE, JSON)
                .header("Authorization", "Basic "+datacite_salasana)
                .POST(HttpRequest.BodyPublishers.ofString(doijson.toString())).build();
        System.out.println(doijson.toString(4));
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode() + response.body());
            if ( response.statusCode() >= 400 ) { // Atron idea
                called.errorcode = response.statusCode() ;
                called.error = response.body();
                return false;
            }
            return true;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //return false;
    }

    /**
     * Vaihdettu logiika niin päin että käyttäjältä tulee valmis doijson. joka
     * lähetetään suoraan datacitelle ilman tarkistuksia!
     *
     * @param doi String päivitettävä DOI
     * @param source DoiJson datacite metadata
     * @return boolean true case success and false case failure.
     *  Note that called.error is to set possible error message from datacite
     */
    public boolean update(String doi, DoiJson source) {
        URI uri = uriFromString(address+"/"+doi); //täydellisessä maailmassa kauttaviivaa ei olisi
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Basic "+datacite_salasana)
                .GET().build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (400 == response.statusCode() || 404  == response.statusCode()) {
                called.error = response.body();
                return false;
            }
            JSONObject doijson =  new JSONObject(response.body());
            JSONObject data = (JSONObject) doijson.get("data");
            JSONObject targetAttributes = (JSONObject) data.get("attributes");
            JSONObject sourceAttributes =  source.attributes;
            Set<String> keys = sourceAttributes.keySet();
            keys.forEach(key -> targetAttributes.put(key, sourceAttributes.get(key)));
            data.put("attributes", targetAttributes); //tässä saattaa olla pari turhaa riviä
            doijson.put("data", data); //varmuuden vuoksi, tämän testaaminen epäoleellista*/
            request = HttpRequest.newBuilder()
                .uri(uri)
                .header(CONTENTTYPE, JSON)
                .header("Authorization", "Basic "+datacite_salasana)
                .PUT(HttpRequest.BodyPublishers.ofString(doijson.toString())).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            called.errorcode = response.statusCode();
            if (399 < called.errorcode  ) {
                called.error = response.body();
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static  URI uriFromString(String address) {
        URI uri = null;
       try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            System.err.println("URISyntaxException: " + e.getMessage());
        }
        return uri;
    }
}
