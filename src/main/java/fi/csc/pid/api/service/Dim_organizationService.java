package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Dim_organization;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class Dim_organizationService {
    public Optional<PanacheEntityBase> getOrgID(String name) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        PanacheQuery<PanacheEntityBase> pq = Dim_organization.find("SELECT id FROM  Dim_organization WHERE name=:name", param);
        return pq.stream().findAny();
    }
}
