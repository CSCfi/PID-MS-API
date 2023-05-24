package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

/**
 * Commented number refer https://doi.org/10.14454/3w3z-sa82 DataCite Metadata Schema Documentation for the
Publication and Citation of Research Data and Other Research Outputs. Version 4.4. by DataCite Metadata Working Group. (2021).
 */
/*@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Attributes {
    public String event;
    public String doi;
    public List<Creator> creators; //2
    public List<Title> titles; //3
    public String publisher; //4
    public int publicationYear; //5
    //String ResourceType; //10
    public List<Subject> subjects; //6
    public List<Contributor> contributors; //7
    public List<Date> dates; //8
    public String language; //9
    public List<AlternateIdentifiers> alternateIdentifiers; //11
    public List<String> size; //13
    public List<Right> rights; //16
    public List<Description> description; //17
    public List<GeoLocation> geoLocation; //18
    public Types types; //10
    public String url;
    public String schemaVersion;
*/
    /**
     * Tämä on onneksi generoitua koodia. Oletus konstruktori
     *
     * @param event String publish, register or hide
     * @param doi String tätä ollaan tekmässä/julkaisemassa/rekisteröimässä
     * @param creators List<Creator>
     * @param titles List<Title>
     * @param publisher String
     * @param publicationYear int
     * @param subjects List<Subject>
     * @param contributors List<Contributor>
     * @param dates List<Date>
     * @param language String
     * @param alternateIdentifiers List<AlternateIdentifiers>
     * @param size  List<String>
     * @param rights List<Right>
     * @param description List<Description>
     * @param geoLocation List<GeoLocation>
     * @param types Types
     * @param url String joksi DOI resolvoituu Most Important!
     * @param schemaVersion String ei nyt päivity vain tätä vaihtamalla, koska tämä on kiinteää koodia
     */
    /*public Attributes(String event, String doi, List<Creator> creators, List<Title> titles, String publisher, int publicationYear, List<Subject> subjects, List<Contributor> contributors, List<Date> dates, String language, List<AlternateIdentifiers> alternateIdentifiers, List<String> size, List<Right> rights, List<Description> description, List<GeoLocation> geoLocation, Types types, String url, String schemaVersion) {
        this.event = event;
        this.doi = doi;
        this.creators = creators;
        this.titles = titles;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.subjects = subjects;
        this.contributors = contributors;
        this.dates = dates;
        this.language = language;
        this.alternateIdentifiers = alternateIdentifiers;
        this.size = size;
        this.rights = rights;
        this.description = description;
        this.geoLocation = geoLocation;
        this.types = types;
        this.url = url;
        this.schemaVersion = schemaVersion;
    }*/
/*
    public String getUrl() {
        return url;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getDoi() {
        return doi;
    }

    public List<Creator> getCreators() {
        return creators;
    }

    public void setCreators(List<Creator> creators) {
        this.creators = creators;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<AlternateIdentifiers> getAlternateIdentifiers() {
        return alternateIdentifiers;
    }

    public void setAlternateIdentifiers(List<AlternateIdentifiers> alternateIdentifiers) {
        this.alternateIdentifiers = alternateIdentifiers;
    }

    public List<String> getSize() {
        return size;
    }

    public void setSize(List<String> size) {
        this.size = size;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public List<Description> getDescription() {
        return description;
    }

    public void setDescription(List<Description> description) {
        this.description = description;
    }

    public List<GeoLocation> getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(List<GeoLocation> geoLocation) {
        this.geoLocation = geoLocation;
    }

    public Types getTypes() {
        return types;
    }

    public void setTypes(Types types) {
        this.types = types;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }
}
*/