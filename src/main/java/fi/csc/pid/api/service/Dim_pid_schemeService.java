package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Dim_pid_scheme;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Dim_pid_schemeService {
    public Dim_pid_scheme getById(int id) {
        return Dim_pid_scheme.findById(id);
    }
}
