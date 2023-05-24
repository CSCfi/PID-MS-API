package fi.csc.pid.api.hallinta;

import fi.csc.pid.api.SecureRandomString;
import fi.csc.pid.api.Util;
import fi.csc.pid.api.entity.Br_dim_service_dim_organization;
import fi.csc.pid.api.entity.Br_dim_service_dim_pid_scheme;
import fi.csc.pid.api.entity.Dim_pid_scheme;
import fi.csc.pid.api.entity.Dim_service;
import fi.csc.pid.api.entity.Dim_url;
import fi.csc.pid.api.service.Dim_organizationService;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import static fi.csc.pid.api.Util.ACCESSDENIED;
import static fi.csc.pid.api.Util.INVALID;
import static fi.csc.pid.api.hallinta.Käyttäjä.Pidtype.DOI;
import static fi.csc.pid.api.hallinta.Käyttäjä.Pidtype.Handle;
import static fi.csc.pid.api.hallinta.Käyttäjä.Pidtype.URN;
import static fi.csc.pid.api.hallinta.Käyttäjä.Pidtype.URNHandle;

@Path("/v1/hallinta/")
public class AdminResource {
    @Inject
    AgroalDataSource defaultDataSource;
    @Inject
    Dim_organizationService orgservice;

    static Hashtable<Käyttäjä.Pidtype, String> landingpages = new Hashtable<>();

    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("user")
    public Response newuser(@HeaderParam("apikey") String apikey, Käyttäjä user) {
        if (INVALID == Util.tarkistaAPIavain(apikey)) return ACCESSDENIED; //full stop

        try {
            Connection connection = defaultDataSource.getConnection();

            Dim_url durl = new Dim_url();
            durl.url = user.url;
            durl.setId(durl.talleta(connection));

            Dim_service dim_service = new Dim_service();
            dim_service.url_id = durl.getId();
            dim_service.name = user.name;
            dim_service.apikey = generateAPIkey();
            dim_service.id = dim_service.talleta(connection);

            Dim_pid_scheme dim_pid_scheme = new Dim_pid_scheme();
            dim_pid_scheme.pid_type = user.pidtype.toString();
            dim_pid_scheme.pid_syntax_regexp = user.pidprefix + "-" + user.pidsuffixtype.toString() + "NNNNNN";
            dim_pid_scheme.landing_page = landingpages.get(user.pidtype);
            dim_service.id = dim_pid_scheme.talleta(connection);

            Br_dim_service_dim_pid_scheme br_dim_service_dim_pid_scheme = new Br_dim_service_dim_pid_scheme();
            br_dim_service_dim_pid_scheme.setDim_pid_scheme_id(dim_pid_scheme.id);
            br_dim_service_dim_pid_scheme.setDim_service_id(dim_service.id);
            br_dim_service_dim_pid_scheme.talleta(connection);

            Br_dim_service_dim_organization br_dim_service_dim_organization = new Br_dim_service_dim_organization();
            br_dim_service_dim_organization.setDim_service_id(dim_service.id);
            // user.org must be 09206320 or EU
            br_dim_service_dim_organization.setDim_organizationid(user.org);
            br_dim_service_dim_organization.persistAndFlush();
            return Response.ok(dim_service.apikey).build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateAPIkey() {
        SecureRandomString srs = new SecureRandomString();
        return srs.generate(64);
    }

    static {
        landingpages.put(URN, "http://urn.fi/" );
        landingpages.put(DOI, "https://www.doi.org/");
        landingpages.put(Handle, "https://hdl.handle.net/");
        landingpages.put(URNHandle, "https://hdl.handle.net/");
    }

}
