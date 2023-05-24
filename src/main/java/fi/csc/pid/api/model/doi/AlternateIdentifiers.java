package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class AlternateIdentifiers {
    String alternateIdentifier;
    String alternateIdentifierType;

    public String getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public void setAlternateIdentifier(String alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
    }

    public String getAlternateIdentifierType() {
        return alternateIdentifierType;
    }

    public void setAlternateIdentifierType(String alternateIdentifierType) {
        this.alternateIdentifierType = alternateIdentifierType;
    }
}
