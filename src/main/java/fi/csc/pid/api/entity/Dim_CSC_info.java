package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dim_CSC_info")
public class Dim_CSC_info extends PanacheEntityBase {
    @Id
    String PIDMiSe_suffix;
    int Checksum;
    String digital_object_type;

    public  Dim_CSC_info() {
        super();
    }

    public  Dim_CSC_info(String PIDMiSe_suffix) {
        this.PIDMiSe_suffix = PIDMiSe_suffix;
    }

    public String getPIDMiSe_suffix() {
        return PIDMiSe_suffix;
    }

    public void setChecksum(int cs) {
        Checksum = cs;
    }
}
