package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Dim_PID;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PIDService {

   public Dim_PID getById(long id) {
       return Dim_PID.findById(id);
   }

   public Dim_PID getByPID(String pid) {
       return Dim_PID.find("identifier_string = ?1", (Object[])new String[]{pid}).firstResult();
   }
}
