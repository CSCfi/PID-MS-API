package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Fact_pid_interlinkage;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Fact_pid_interlinkageService {
    public  Fact_pid_interlinkage getById(long pid) {
        return  Fact_pid_interlinkage.findById(pid);
    }

    public List<Fact_pid_interlinkage> getByUrlId(int url_id) {
        return  Fact_pid_interlinkage.find("url_id = ?1",url_id).list();
    }
}
