package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.validation.Valid;
@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Date {

    public enum DateType {
        Accepted,
        Available,
        Copyrighted,
        Collected,
        Created,
        Issued,
        Submitted,
        Updated,
        Valid,
        Withdrawn,
        Other
    }

    DateType dateType;
    java.util.Date date;

    public DateType getDateType() {
        return dateType;
    }

    public void setDateType(DateType dateType) {
        this.dateType = dateType;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }
}
