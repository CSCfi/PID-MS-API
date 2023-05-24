package fi.csc.pid.api.model;

import fi.csc.pid.api.entity.Dim_PID;

public class Sisältö {
    private long id;
    final int status;
    final String error;
    Dim_PID pid;
    StringBuffer sb;
    String tarkistettava;
    int tarkiste;

    /**
     * Virhekontruktori
     *
     * @param status int code < 0
     * @param error  String error message
     */
    public Sisältö(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public long getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Dim_PID getPid() {
        return pid;
    }

    public StringBuffer getSb() {
        return sb;
    }

    public String getTarkistettava() {
        return tarkistettava;
    }

    public int getTarkiste() {
        return tarkiste;
    }

    /**
     * Oletus (hyvin meni) konstruktori
     *
     * @param status int code = 0
     * @param error String null
     * @param pid Dim_PID pihvi
     * @param sb StringBuffer jatkoon
     * @param tarkistettava String jatkoon
     * @param tarkiste int jatkoon
     */
    public Sisältö(int status, String error, Dim_PID pid, StringBuffer sb, long id,
                   String tarkistettava, int tarkiste) {
        this.status = status;
        this.error = error;
        this.pid = pid;
        this.sb = sb;
        this.id = id;
        this.tarkistettava = tarkistettava;
        this.tarkiste = tarkiste;
    }
}

