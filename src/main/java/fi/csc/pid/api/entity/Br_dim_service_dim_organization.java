package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

//Id on entityn ominaisuus. Ne on siis kytketty yhteen.
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "br_dim_service_dim_organization")
public class Br_dim_service_dim_organization extends PanacheEntityBase {
    @Id
    int dim_serviceid;
    String dim_organizationid;

    public void setDim_organizationid(String dim_organizationid) {
        this.dim_organizationid = dim_organizationid;
    }

    public int getService_id() {
        return dim_serviceid;
    }

    public void setDim_service_id(int dim_service_id) {
        this.dim_serviceid = dim_service_id;
    }

    public String getOrganizationid() {
        return dim_organizationid;
    }
}
