package fi.csc.pid.api.hallinta;

public class Käyttäjä {
    public String url;
    public String name;
    public String org; //CSC or EU
    public String pidprefix;
    public Pidsuffixtype pidsuffixtype;
    public Pidtype pidtype;

    public enum Pidtype {URN, URNHandle, Handle, DOI}
    public enum Pidsuffixtype {UUID, YYYYMM}
}
