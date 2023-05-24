package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Br_dim_service_dim_organization;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Br_dim_service_dim_organizationService {
    public List<Br_dim_service_dim_organization> listALL() {
        return Br_dim_service_dim_organization.listAll();
    }
}
