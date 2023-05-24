package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Fact_touched;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Historia palvelu
 */

@ApplicationScoped
public class Fact_touchedService {
    public List<Fact_touched> getById(long id) {
        return Fact_touched.find("dim_PIDinternal_id = ?1", new Long[]{id}).list();
    }
}
