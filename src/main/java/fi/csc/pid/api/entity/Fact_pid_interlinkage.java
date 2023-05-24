package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.*;
import jakarta.persistence.Id;

/**
 * Toistaiseksi ei käytössä, koska hiberbate EI osannut tallentaa.
 * Korvattu db-package:ssa olevalla vastaavalla, mutta joka hyvänä päivänä yhdistetään tähän,
 * sillä tälläisessä entityssakin voi olla tallenna-medoti!
 */

@Entity
@Table(name = "fact_pid_interlinkage")
public class Fact_pid_interlinkage extends PanacheEntityBase {
    @Id //@JoinColumn(name = "internal_id", nullable=false)
    public long dim_PIDinternal_id;
    public int url_id;
    public String dim_CSC_infoPIDMiSe_suffix;

     public Fact_pid_interlinkage() {
      super();
     }

    public Fact_pid_interlinkage(long dim_PIDinternal_id, int url_id, String suffix) {
         super();
        this.dim_PIDinternal_id = dim_PIDinternal_id;
        this.url_id = url_id;
        this.dim_CSC_infoPIDMiSe_suffix = suffix;
    }


    public int getUrlId() {
        return url_id;
    }

}
