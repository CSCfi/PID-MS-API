package fi.csc.pid.api.entity;

import java.io.Serializable;

public class Fact_touchedPK implements Serializable {
    protected String dim_organizationid;
    protected long dim_PIDinternal_id;
    protected int dim_serviceid;

    public Fact_touchedPK() {}

    public Fact_touchedPK(String dim_organizationid, long dim_PIDinternal_id, int dim_serviceid) {
        this.dim_organizationid = dim_organizationid;
        this.dim_PIDinternal_id = dim_PIDinternal_id;
        this.dim_serviceid = dim_serviceid;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Fact_touchedPK )) {
            return false;
        }
        Fact_touchedPK ftpk = (Fact_touchedPK) o;
        return dim_organizationid.equals(ftpk.dim_organizationid) &&
                Long.compare(dim_PIDinternal_id, ftpk.dim_PIDinternal_id) == 0 &&
                Integer.compare(dim_serviceid, ftpk.dim_serviceid) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 7;
        result = prime * result +  ((dim_organizationid == null) ? 0 : dim_organizationid.hashCode());
        result = prime * result + (int) (dim_PIDinternal_id ^ (dim_PIDinternal_id >>> 32));
        result = (prime * result) + dim_serviceid;
        return result;
    }
}
