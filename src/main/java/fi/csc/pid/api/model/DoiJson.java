package fi.csc.pid.api.model;

//import fi.csc.pid.api.doi.DOIResource;
import jakarta.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.jboss.logging.Logger;

import static fi.csc.pid.api.Util.URLMISSING;

public class DoiJson {
    public JSONObject doi;
    public JSONObject data;
    public JSONObject attributes;
    private String url;
    public Response errorResponse;
    public boolean error;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    private static final Logger LOG = Logger.getLogger(DoiJson.class);

    public DoiJson(String doijson, int id, int SD) {

        try {
            this.doi = new JSONObject(doijson);
            this.data = (JSONObject) this.doi.get("data");
            this.attributes = (JSONObject) this.data.get("attributes");
            if (null == this.attributes) {
                LOG.error("Attributes are null.");
                this.errorResponse = Response.status(400, "Attributes are null.").build();
                this.error = true;
            }
            this.url = (String) attributes.get("url");
            if (null == this.url) {
                LOG.error("DOIlta puuttuu URL!");
                this.errorResponse = URLMISSING;
                if (SD == id)
                    this.error = false;
                else
                    this.error = true;
            } else {
                this.error = false;
            }
        } catch (JSONException e) {
            LOG.error("Poikkeus DOI JSON parsinnassa: "+e.getMessage());
            if (SD == id)
                this.error = false;
            else
                this.error = true;
            this.errorResponse = Response.status(400, "Please follow datacite schema: " + e.getMessage()).build();
        }
    }


}
