package fi.csc.pid.api;

import fi.csc.pid.api.entity.Br_dim_service_dim_organization;
import fi.csc.pid.api.service.Br_dim_service_dim_organizationService;
import fi.csc.pid.api.entity.Br_dim_service_dim_pid_scheme;
import fi.csc.pid.api.service.Br_dim_service_dim_pid_schemeService;
import fi.csc.pid.api.entity.Dim_service;
import fi.csc.pid.api.service.Dim_pid_schemeService;
import fi.csc.pid.api.service.ServiceService;
import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import java.util.*;

@ApplicationScoped
public class ApplicationLifecycle {

     @Inject
     ServiceService dimservice;
     @Inject
     Br_dim_service_dim_pid_schemeService bdsdpss;
     @Inject
     Dim_pid_schemeService dpss;
     @Inject
    Br_dim_service_dim_organizationService bdsdos;

     private static final Logger LOG = Logger.getLogger(ApplicationLifecycle.class);
     public static final Hashtable<String, Integer> apikeyservice = new Hashtable<String, Integer>();
     public static final Hashtable<String, Integer> servicescheme = new Hashtable<String, Integer>();
     public static final Hashtable<Integer, String> serviceorganization  = new Hashtable<Integer, String>();

     void onStart(@Observes StartupEvent event) {

          List<Dim_service> lds = dimservice.getServices();
          lds.forEach(ds -> {
                       //LOG.info(ds.getApikey());
                       apikeyservice.put(ds.getApikey(), ds.getId());
                  });
          List<Br_dim_service_dim_pid_scheme>  lbdsdps = bdsdpss.getAll();
          lbdsdps.forEach(row ->
          {   int serviceid = row.getService_id();
              int schemeid = row.getScheme_id();
              LOG.info("SchemeID: "+schemeid);
              var type = dpss.getById(schemeid).pid_type;
              LOG.info("serviceschemePUT "+ serviceid + type);
              servicescheme.put(serviceid + type, row.getScheme_id());
          });
          List<Br_dim_service_dim_organization> lbdsdo = bdsdos.listALL();
          lbdsdo.forEach(row ->
                  serviceorganization.put(row.getService_id(), row.getOrganizationid()));
     }

     /**
      * Kaivetaan oikea scheme tietokannasta.
      *
      * @param id int Service Id
      * @param type String URL || DOI tms. Mitä nyt scheme taulun type-kentässä on
      * @return int scheme taulun Id, jossa siis type match
      */
     public static int scheme(int id, String type) {
          Integer skeema = servicescheme.get(id+type);
          if (null == skeema) return -2;
          return skeema; // jos olisi monta, tietokannan sisällössä on virhe, mutta ei puututa siihen
     }

}
