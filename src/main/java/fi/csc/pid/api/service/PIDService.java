package fi.csc.pid.api.service;

import fi.csc.pid.api.entity.Dim_PID;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PIDService {

      private static final Logger LOG = Logger.getLogger(PIDService.class);

   public Dim_PID getById(long id) {
       return Dim_PID.findById(id);
   }

    public Dim_PID getByPID(String pid) {
        try {
            return Dim_PID.find("identifier_string = ?1", (Object[]) new String[]{pid}).firstResult();
        } catch (NullPointerException npe) {
            LOG.info("Should be full URN but was "+pid);
            long id;
            try {
                id = Long.parseLong(pid);
            } catch (NumberFormatException nfe ) {
                LOG.info("And was NOT long");
                return null;
            }
            return Dim_PID.findById(id);
        }
    }
}
