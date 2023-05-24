package fi.csc.pid.api;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DBService {
    public String greeting(String name) {
        return "hello " + name;
    }
}
