package fi.csc.pid.api.entity;

/**
 * Tämä olio on GetPid-metodin paluuarvo (joka tietenkin esitetään JSONina)
 */
public class GetPidTuloste {
    public String pid;
    public String pid_type;

    public GetPidTuloste(String pid, String pid_type) {
        this.pid = pid;
        this.pid_type = pid_type;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid_type() {
        return pid_type;
    }

    public void setPid_type(String pid_type) {
        this.pid_type = pid_type;
    }
}
