package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@RegisterForReflection
@Entity
@IdClass(Fact_touchedPK.class)
@Table(name = "fact_touched")
public class Fact_touched extends PanacheEntityBase {
    @Id
    public String dim_organizationid;
    @Id
    public long dim_PIDinternal_id;
    @Id
    public int dim_serviceid;
    public String old_values;
    public Timestamp timestamp;

    public Fact_touched() {
        //tarkoituksella tyhjä: "a default constructor is required by the JSON serialization layer"
    }

}
