package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Right {
    String rights;
    String rightsURI;

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getRightsURI() {
        return rightsURI;
    }

    public void setRightsURI(String rightsURI) {
        this.rightsURI = rightsURI;
    }
}
