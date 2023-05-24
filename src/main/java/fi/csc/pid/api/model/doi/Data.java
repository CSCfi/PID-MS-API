package fi.csc.pid.api.model.doi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.google.gson.InstanceCreator;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@RegisterForReflection
public class Data {
    String id; //DOI
    String type; //dois
    public Attributes attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    /**
     * Tämä oletus konstruktori ilmeisesti tarvittiin
     *
     * @param id String tähän tulee doi kun sen teen
     * @param type String dois
     * @param attributes Attributes pihvi eli metatiedot http://datacite.org/schema/kernel-4
     */
    /*public Data(String id, String type, Attributes attributes) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
    }*/

    /**
     * HUOM!!! DOI on dataciten schemassa kahdessa eri kohdassa ja tämä yksi setteri asettaa molemmat
     *
     * @param doi String 10....
     */
    public void setDOI(String doi) {
        this.id = doi;
        this.attributes.setDoi(doi);
    }

    public Attributes getAttributes() {
        return attributes;
    }

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

        public Attributes() {
            // No args constructor for Attributes for gson
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public String getUrl() {
            return url;
        }
    }

    /*
    public class InstanceCreatorForAttributes implements InstanceCreator<Attributes> {
        private final Data a;
        public InstanceCreatorForAttributes(Data a)  {
            this.a = a;
        }

        public Data.Attributes createInstance(Type type) {
            return a.new Attributes();
  }

     */
}

