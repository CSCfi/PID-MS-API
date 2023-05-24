package fi.csc.pid.api.entity;


import io.quarkus.runtime.annotations.RegisterForReflection;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Syöte {
    public String url;
    public String type;
    public int persist;

    public int getPersist() {
        return persist;
    }

    public void setPersist(int persist) {
        this.persist = persist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String URL) {
        this.url = URL;
    }
}
