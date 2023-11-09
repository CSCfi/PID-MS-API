package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Dim_url;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Dim_URLService {
    public Dim_url getById(int id) {
        return  Dim_url.findById(id);
    }

    public long countByURL(String URL) {
        return  Dim_url.find("url = ?1", (Object[])new String[]{URL}).count();
    }

    public List<Dim_url> getByURL(String URL) {
        return  Dim_url.find("url = ?1", (Object[])new String[]{URL}).list();
    }
}
