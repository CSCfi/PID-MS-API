package fi.csc.pid.api.doi;

import fi.csc.pid.api.ApplicationLifecycle;
import fi.csc.pid.api.Util;
import fi.csc.pid.api.db.FactPiDInterlinkage;
import fi.csc.pid.api.entity.Dim_CSC_info;
import fi.csc.pid.api.entity.Dim_PID;
import fi.csc.pid.api.entity.Dim_pid_scheme;
import fi.csc.pid.api.entity.Dim_url;
import fi.csc.pid.api.entity.Fact_pid_interlinkage;
import fi.csc.pid.api.model.DoiJson;
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

import java.sql.Connection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static fi.csc.pid.api.PIDResource.NOCONNECTION;
import static fi.csc.pid.api.Util.ACCESSDENIED;
import static fi.csc.pid.api.Util.INVALID;
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
        @ConfigProperty(name = "SDSALASANA")
        String SDSALASANA;
        @ConfigProperty(name = "SD")
        int SD;

        private static final String BPDEMO = "https://bp-demo.rahtiapp.fi/";
        public static final String MUSTBEDOI = "The pathparam must be 10/suffix";
        //public static final int SD = 8; //SD DOI can be draft
        private static final Logger LOG = Logger.getLogger(DOIResource.class);
    public String error; //from datacite
    public int errorcode; //from datacite
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

        DoiJson dj = new DoiJson(doijson, id, SD);
        String URL;
        if (dj.error) {
            LOG.error("DoiJson parsing error");
            //ei toivuta
            return dj.errorResponse;
        } else {
            URL = dj.getUrl();
            LOG.info("DoiJson parsing ok: "+URL);
        }
        if (null == URL) {
            if (SD == id) { //SD
                URL = BPDEMO+randomString(10);
            } else {
                LOG.error("URL was null");
                return Response.status(400, "URL was null").build();
            }
        }
        //Tarkistetaan ettei URLille ole jo DOI
        long duc = dus.countByURL(URL);
        if (0 < duc) { //URLilla on jo joku PID
            List<Dim_url> urllist = dus.getByURL(URL);
            List<String> aldoi = urllist.stream().map(this::tarkistaDOI).toList();
            LOG.info("aldoi size: "+aldoi.size());
            String doiorempty = aldoi.stream().filter(d -> !d.isEmpty()).collect(Collectors.joining(", "));
            LOG.info("doiorempty: "+doiorempty);
            if (!doiorempty.isEmpty()) {
                return Response.status(400, "URL already have DOI(s): " + doiorempty).build();
            }
        }

        int scheme = ApplicationLifecycle.scheme(id, "DOI");
        if (scheme < 0) {
            LOG.warn("DOI scheme missing: " + id);
            return Response.status(500, "DOI scheme missing:" + scheme).build();
        }
        Dim_pid_scheme dps = dpss.getById(scheme);
        String pidSyntaxRegexp = dps.pid_syntax_regexp;
        String alku = pidSyntaxRegexp.substring(0, 12); //10.23729/fd- or 10.24340/sd-

        try {
            Connection connection = defaultDataSource.getConnection();
            Util util = new Util();
            Sisältö content = util.realCreate(id, scheme, connection);
            String sisältö = content.getTarkistettava() + content.getTarkiste();
            if (pidSyntaxRegexp.contains("UUID")) {
                sisältö = Util.UUID(sisältö);
            }
            String key = (SD == id ? SDSALASANA : datacite_salasana); //5 = SD Tuotanto and 8 test
            DOI d = new DOI(this, datacite_host, key); //pihvi alkaa
            String dois = alku + sisältö;
            if (d.create(dois, dj.doi, dj.data, dj.attributes)) {
                content.getPid().identifier_string=dois;
                content.getPid().update(connection);
                LOG.info("URL="+URL);
                Dim_url durl = new Dim_url(URL);
                if (URL.startsWith(BPDEMO))
                    durl = new Dim_url(URL+dois);
                else
                    durl = new Dim_url(URL);
                durl.persistAndFlush();
                Dim_CSC_info dci = new Dim_CSC_info(sisältö);
                dci.setChecksum(content.getTarkiste());
                dci.persistAndFlush();
                FactPiDInterlinkage fpi = new FactPiDInterlinkage(content.getId(), durl.getId(), dci.getPIDMiSe_suffix());
                fpi.tallenna(connection);
                return Response.ok(dois).build();
            } else
                return Response.status(this.errorcode, "Datacite failed: "+this.error).build();
        } catch (java.sql.SQLException e) {
            LOG.error(e.getMessage());
            LOG.error(NOCONNECTION); //??
            return Response.status(501, NOCONNECTION).build();
        }
    }

    /**
     *
     * Generated by claude.ai
     * @return Random String
     */
    private String randomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * Update URL of the DOI
     *
     * @param apikey String the secret
     * @param prefix String 10.2...
     * @param suffix String fd-UUID
     * @param doijson String new metadata
     * @return HTTP Response new URL case ok or error.
     */
    @Transactional
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{prefix}/{suffix}") //bacause DOI, prefix is allways 10
    public Response update(@HeaderParam("apikey") String apikey, @PathParam("prefix") String prefix,
                           @PathParam("suffix") String suffix, String doijson) {
        int id = Util.tarkistaAPIavain(apikey);
        if (INVALID == id) return ACCESSDENIED; //full stop

        if ((null == prefix) || (null == suffix))
            return Response.status(400, MUSTBEDOI).build();
        if (!prefix.startsWith("10")) //10.82614/...
            return Response.status(400, "This is DOI API: the prefix must start with 10").build();
        final String DOI = prefix+"/"+suffix;
        DoiJson dj = new DoiJson(doijson, id, SD);
        if (dj.error) {
            LOG.error("doijson parsing error");
            return dj.errorResponse;
        }
         // Syöte tarkistettu
        Dim_PID pid = ps.getByPID(DOI);
        if (null == pid) {
            return Response.status(400, "Can't find DOI from database").build();
        }
        Fact_pid_interlinkage fpil = fpis.getById(pid.internal_id);
        Dim_url du = dus.getById(fpil.url_id);
        Boolean noturlupdate = du.url.equals(dj.getUrl());
        String key = (SD == id ? SDSALASANA : datacite_salasana); //5 = SD Tuotanto and 8 test
        DOI d = new DOI(this, datacite_host, key);
        if (d.update(DOI, dj)) {
            if (!noturlupdate) { //not not = urlupdate
                du.url = dj.getUrl();
                du.persistAndFlush();
            }
            return Response.ok(dj.getUrl()).build();
        } else {
            LOG.error("Datacite error: " + this.error);
            return Response.status(400, "Datacite update  failed: " + this.error).build();
        }
    }



    /**
     * Palauttaa merkkijonon joka sisältää olemassa olevan DOIn samalle parametrina tulevalle Dim_url:llille
     *
     * @param durl Dim_url tietokantatauluolio
     * @return String DOI or empty String
     */
    String tarkistaDOI(Dim_url durl) {
            List<Fact_pid_interlinkage> lfpi = fpis.getByUrlId(durl.getId());
            StringBuilder sb = new StringBuilder();
            lfpi.stream().map(this::tarkistaDOI2).forEach(sb::append);
            return sb.toString();
    }

    /**
     * Palauttaa merkkijonon joka sisältää olemassa olevan DOIn Fact_pid_interlinkage tauluoliolle
     *
     * @param fpi Fact_pid_interlinkage tauluolio
     * @return String DOI or empty String
     */
    String tarkistaDOI2(Fact_pid_interlinkage fpi) {
        Dim_PID pid = ps.getById(fpi.dim_PIDinternal_id);
        if (pid.identifier_string.startsWith("10"))
            return pid.identifier_string;
        else
            return "";
    }
}
