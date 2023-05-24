package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class GeoLocation {
    public record geoLocationPoint(double pointLongitude, double pointLatitude) {
    }

    geoLocationPoint glp;

    public record polygonPoint(double pointLongitude, double pointLatitude) {
    }
    public record geoLocationPolygon(List<polygonPoint> lpp) {

    }
    geoLocationPolygon glPolycon;

    public geoLocationPoint getGlp() {
        return glp;
    }

    public void setGlp(geoLocationPoint glp) {
        this.glp = glp;
    }

    public geoLocationPolygon getGlPolycon() {
        return glPolycon;
    }

    public void setGlPolycon(geoLocationPolygon glPolycon) {
        this.glPolycon = glPolycon;
    }
}
