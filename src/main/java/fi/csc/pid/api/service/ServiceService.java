package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Dim_service;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ServiceService {

    public List<Dim_service> getServices() {
        return Dim_service.listAll();
    }

}
