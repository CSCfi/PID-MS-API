package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Br_dim_service_dim_pid_scheme;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Br_dim_service_dim_pid_schemeService {
    public List<Br_dim_service_dim_pid_scheme> getAll() {
        return Br_dim_service_dim_pid_scheme.listAll();
    }

    //2020-10-16 16:55:25,905 WARN  [org.hib.hql.int.QuerySplitter] (main) HHH000183: no persistent classes found for query class: FROM fi.csc.pid.api.entity.Br_dim_service_dim_pid_scheme
    //Koska perjantai-ilta ja klo 17.
}
