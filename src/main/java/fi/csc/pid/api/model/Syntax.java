package fi.csc.pid.api.model;

public record Syntax(boolean alaviiva, boolean success, boolean ishandle, String urn, String handle) {

    public static Syntax virhe(boolean alaviiva, boolean success, boolean ishandle) {
        return new Syntax(alaviiva, success, ishandle,null, null);
    }
}
