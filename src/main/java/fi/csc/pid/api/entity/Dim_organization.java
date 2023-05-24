package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dim_organization")
public class Dim_organization extends PanacheEntityBase {
    @Id
    String id;
    String name;
    String apikey;
}
