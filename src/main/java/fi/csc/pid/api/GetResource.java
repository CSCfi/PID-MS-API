package fi.csc.pid.api;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.annotation.JacksonFeatures;
import fi.csc.pid.api.entity.Dim_PID;
import fi.csc.pid.api.entity.Dim_pid_scheme;
import fi.csc.pid.api.entity.Fact_pid_interlinkage;
import fi.csc.pid.api.entity.GetPidTuloste;
import fi.csc.pid.api.service.Dim_pid_schemeService;
import fi.csc.pid.api.service.Fact_pid_interlinkageService;
import fi.csc.pid.api.service.PIDService;
import org.jboss.resteasy.annotations.jaxrs.HeaderParam;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
//import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import static fi.csc.pid.api.Util.URLMISSING;
import static fi.csc.pid.api.doi.DOIResource.MUSTBEDOI;

import fi.csc.pid.api.service.Dim_URLService;
import fi.csc.pid.api.entity.Dim_url;

//import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/get/v1/pid")
public class GetResource {
    private static final Logger LOG = Logger.getLogger(GetResource.class);

    @Inject
    Dim_URLService dus;
    @Inject
    Fact_pid_interlinkageService fpis;
    @Inject
    PIDService ps;
    @Inject
    Dim_pid_schemeService dpss;

    /**
     * <a href="https://wiki.eduuni.fi/display/CSCdatamanagementoffice/PID-service+REST+API+description">https://wiki.eduuni.fi/display/CSCdatamanagementoffice/PID-service+REST+API+description</a>
     * Tämä siis palauttaa mahdollisen olemassa olevan PIDin.
     *
     * @param apikey  String palvelulle spesifinen PID palvelun käyttötunniste
     * @param URL String resurssin url
     * @return String JSON pid, pid_type
     */
    /*@JacksonFeatures(serializationDisable = {SerializationFeature.FAIL_ON_EMPTY_BEANS})*/
    @Transactional
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@Path("/get/v2/pid/")
    public Response getPID(@HeaderParam("apikey") String apikey, @QueryParam("URL") String URL){
        int sid = Util.tarkistaAPIavain(apikey);
        if (Util.INVALID == sid) return Util.ACCESSDENIED;
        if (null == URL)
            return URLMISSING;
        //oletetaan URL URLencoodattuna
        String decodedURL = URLDecoder.decode(URL, StandardCharsets.UTF_8);

        List<Dim_url> ldu = dus.getByURL(decodedURL);
        if (null == ldu || ldu.isEmpty()) {
            return Response.status(500, "Unknown URL").build();
        }
        List<Fact_pid_interlinkage> lfpi = fpis.getByUrlId(ldu.getFirst().getId());
        if (lfpi.isEmpty()) { //should not happen
           return Response.status(500, "URL has no PID").build();
        }
        Fact_pid_interlinkage fpi = lfpi.getFirst();
        Dim_PID dp = ps.getById(fpi.dim_PIDinternal_id);
        if (null == dp) { // very bad error in database
            return Response.status(500, "URL exist but no PID!").build();
        }
        Dim_pid_scheme dps = dpss.getById(dp.dim_pid_scheme_id);
        //LOG.info("Kyselyn pitäisi palauttaa: " + dp.identifier_string);
        GetPidTuloste gpt = new GetPidTuloste(dp.identifier_string, dps.pid_type);
        return Response.ok(gpt ).build();
    }

    /**
     *  <a href="https://wiki.eduuni.fi/display/CSCdatamanagementoffice/PID-service+REST+API+description">https://wiki.eduuni.fi/display/CSCdatamanagementoffice/PID-service+REST+API+descriptio</a>
     *
     * @param apikey String palvelulle spesifinen PID palvelun käyttötunniste
     * @param PID String tunniste
     * @return String resurssin url
     */
    @JacksonFeatures(serializationDisable = {SerializationFeature.FAIL_ON_EMPTY_BEANS})
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{pid}")
    public Response getURL(@HeaderParam("apikey") String apikey,@PathParam("pid") String PID) {
        int sid = Util.tarkistaAPIavain(apikey);
        if (Util.INVALID == sid) return Util.ACCESSDENIED;
        if (null == PID) {
            return Response.status(500, "PID is missing").build();
        }
        return resolvoi(PID);
    }

    /**
     * Local DOI resolver
     * Please use doi.org instesd of this
     * This is done because of the CSCPID-37
     *
     * @param apikey String palvelulle spesifinen PID palvelun käyttötunniste
     * @param prefix  String PID prefix
     * @param suffix  String   PID suffix
     * @return String resurssin url
     */
     @JacksonFeatures(serializationDisable = {SerializationFeature.FAIL_ON_EMPTY_BEANS})
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{prefix}/{suffix}" )
    public Response getURL(@HeaderParam("apikey") String apikey,@PathParam("prefix") String prefix,
                           @PathParam("suffix") String suffix) {
         int sid = Util.tarkistaAPIavain(apikey);
        if (Util.INVALID == sid) return Util.ACCESSDENIED;
        if ((null == prefix) || (null == suffix))
            return Response.status(400, MUSTBEDOI).build();
        final String DOI = prefix+"/"+suffix;
        return resolvoi(DOI);
     }

    /**
     * Resolvoi lokaalisti URNin tai DOIn (mahdollisesti muunkin PIDin).
     * Oikeasti ainakin DOIt pitäisi reslvoida doi.org:issa.
     *
     * @param pid String URN or DOI, handle may work too
     * @return Response String URL
     */
     Response resolvoi(String pid) {
         LOG.debug("vasta resolvoinnin alussa: "+pid);
         Dim_PID dp = ps.getByPID(pid);
         if (null == dp) {
             return Response.status(404, "Unknown PID").build();
         }
         LOG.debug("PID löytyi");
         Fact_pid_interlinkage fpi = fpis.getById(dp.internal_id);
         if (null == fpi) { // very bad error in database
             return Response.status(500, "Pid exist but no URL").build();
         }
         LOG.debug("melkein resolvoitu");
         Dim_url du = dus.getById(fpi.url_id);
         if (null == du) { // very bad error in database
             return Response.status(500, "URL is missing").build();
         }
         return Response.ok(du.url).build();
     }
}
