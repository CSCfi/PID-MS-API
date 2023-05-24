package fi.csc.pid.api.doi;

import fi.csc.pid.api.ApplicationLifecycle;
import fi.csc.pid.api.PIDResource;
import fi.csc.pid.api.Util;
import fi.csc.pid.api.db.FactPiDInterlinkage;
import fi.csc.pid.api.entity.Dim_CSC_info;
import fi.csc.pid.api.entity.Dim_PID;
import fi.csc.pid.api.entity.Dim_pid_scheme;
import fi.csc.pid.api.entity.Dim_url;
import fi.csc.pid.api.entity.Fact_pid_interlinkage;
import fi.csc.pid.api.model.Sisältö;
import fi.csc.pid.api.service.Dim_URLService;
import fi.csc.pid.api.service.Dim_pid_schemeService;
import fi.csc.pid.api.service.Fact_pid_interlinkageService;
import fi.csc.pid.api.service.PIDService;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.UUID;

import static fi.csc.pid.api.PIDResource.NOCONNECTION;
import static fi.csc.pid.api.Util.ACCESSDENIED;
import static fi.csc.pid.api.Util.INVALID;
import static fi.csc.pid.api.Util.URLMISSING;
import org.jboss.logging.Logger;

@Path("/v1/pid/doi")
public class DOIResource {

       @Inject
       AgroalDataSource defaultDataSource;
       @Inject
       Dim_pid_schemeService dpss;
       @Inject
       PIDService ps;
       @Inject
       Dim_URLService dus;
        @Inject
        Fact_pid_interlinkageService fpis;
        @ConfigProperty(name = "datacite.host")
        String datacite_host;
        @ConfigProperty(name = "datacite.salasana")
        String datacite_salasana;
        private static final Logger LOG = Logger.getLogger(DOIResource.class);
    public String error; //from datacite
    /** @ Transactional
     * Register new DOI to datasite
     *
     * @param apikey String secret from database
     * @param doijson String Metadata for DOI
     * @return valid DOI or error
     */
    /*@JacksonFeatures(serializationDisable = {SerializationFeature.FAIL_ON_EMPTY_BEANS})
     Cannot invoke "fi.csc.pid.api.model.doi.Attributes.getUrl()" because the return value of "fi.csc.pid.api.model.doi.Data.getAttributes()" is null
     */
    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response add(@HeaderParam("apikey") String apikey, String doijson) {
        int id = Util.tarkistaAPIavain(apikey);
        if (INVALID == id) return ACCESSDENIED; //full stop

        JSONObject doi =  new JSONObject(doijson);
        JSONObject data = (JSONObject) doi.get("data");
        JSONObject attributes = (JSONObject) data.get("attributes");
        String url =  (String) attributes.get("url");
        if (null == url) {
            LOG.error("DOIlta puuttuu URL!");
            return URLMISSING;
        }
        //Tässä ei tarkisteta URLin olemassaoloa
        // Eli myönnetään DOI vaikka samalla URLilla olisi jo URN tai handle
        int scheme = ApplicationLifecycle.scheme(id, "DOI");
        if (scheme < 0) {
            LOG.warn("DOI scheme missing: " + id);
            return Response.status(500, "DOI scheme missing:" + scheme).build();
        }
        Dim_pid_scheme dps = dpss.getById(scheme);
        String pidSyntaxRegexp = dps.pid_syntax_regexp;
        String alku = pidSyntaxRegexp.substring(0, 12); //10.23729/fd-
        /*if (!alku.equals("10.23729/fd-")) {
            LOG.warn("DOI alku is " + alku);
        }*/
        try {
            Connection connection = defaultDataSource.getConnection();
            Util util = new Util();
            Sisältö content = util.realCreate(id, scheme, connection);
            String sisältö = content.getTarkistettava() + content.getTarkiste();
            if (pidSyntaxRegexp.contains("UUID")) {
                byte[] nameSpaceBytes = "Fairdata".getBytes(StandardCharsets.UTF_8);
                byte[] nameBytes = sisältö.getBytes(StandardCharsets.UTF_8);
                byte[] result = Util.joinBytes(nameSpaceBytes, nameBytes);
                UUID uuid = UUID.nameUUIDFromBytes(result);
                sisältö = uuid.toString();
            }
            DOI d = new DOI(this, datacite_host, datacite_salasana); //pihvi alkaa
            String dois = alku + sisältö;
            if (d.create(dois, doi, data, attributes)) {
                content.getPid().identifier_string=dois;
                content.getPid().update(connection);
                Dim_url durl = new Dim_url();
                durl.url = url;
                durl.persistAndFlush();
                Dim_CSC_info dci = new Dim_CSC_info(sisältö);
                dci.setChecksum(content.getTarkiste());
                dci.persistAndFlush();
                FactPiDInterlinkage fpi = new FactPiDInterlinkage(content.getId(), durl.getId(), dci.getPIDMiSe_suffix());
                fpi.tallenna(connection);
                return Response.ok(dois).build();
            }
            else
                return Response.status(400, "Datacite failed: "+this.error).build();
        } catch (java.sql.SQLException e) {
            LOG.error(e.getMessage());
            LOG.error(NOCONNECTION); //??
            return Response.status(501, NOCONNECTION).build();
        }
    }

    /**
     * Update URL of the DOI
     *
     * @param apikey String the secret
     * @param prefix String 10.2...
     * @param suffix String fd-UUID
     * @param URL String new URL
     * @return HTTP Response new URL case ok or error.
     */
    @Transactional
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{prefix}/{suffix}") //bacause DOI, prefix is allways 10
    public Response update(@HeaderParam("apikey") String apikey, @PathParam("prefix") String prefix,
                           @PathParam("suffix") String suffix, String URL) {
        int id = Util.tarkistaAPIavain(apikey);
        if (INVALID == id) return ACCESSDENIED; //full stop

        if ((null == prefix) || (null == suffix))
            return Response.status(400, "The pathparam must be 10/suffix").build();
        if (!prefix.startsWith("10")) //10.82614/...
            return Response.status(400, "This is DOI API: the prefix must start with 10").build();
        final String DOI = prefix+"/"+suffix;
        if (null == URL) {
            LOG.warn("URL puuttuu!");
            return URLMISSING;
        }
        JSONObject jo = new JSONObject(URL);
        String url = jo.getString("URL");
        if (null == url) {
            LOG.warn("Syötteen pitäisi olla JSON olio {URL: arvo}!");
            return URLMISSING;
        } // Syöte tarkistettu
        Dim_PID pid = ps.getByPID(DOI);
        if (null == pid) {
            return Response.status(400, "Can't find DOI from database").build();
        }
        Fact_pid_interlinkage fpil = fpis.getById(pid.internal_id);
        DOI d = new DOI(this, datacite_host, datacite_salasana);
        if (d.update(DOI, url)) {
            Dim_url du = dus.getById(fpil.url_id);
            du.url = url;
            du.persistAndFlush();
            return Response.ok(url).build();
        } else
            return Response.status(400, "Datacite update  failed: "+this.error).build();
    }
}
