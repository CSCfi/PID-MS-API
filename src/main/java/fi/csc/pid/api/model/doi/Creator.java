package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Creator {

    public enum NameType {
        Organizational,
        Personal
    }

    String name;
    NameType nameType;
    String nameIdentifier;
    String nameidentifierScheme;
    String affiliation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NameType getNameType() {
        return nameType;
    }

    public void setNameType(NameType nameType) {
        this.nameType = nameType;
    }

    public String getNameIdentifier() {
        return nameIdentifier;
    }

    public void setNameIdentifier(String nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public String getNameidentifierScheme() {
        return nameidentifierScheme;
    }

    public void setNameidentifierScheme(String nameidentifierScheme) {
        this.nameidentifierScheme = nameidentifierScheme;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
}
