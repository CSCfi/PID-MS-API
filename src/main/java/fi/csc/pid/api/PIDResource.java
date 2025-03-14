package fi.csc.pid.api;

import fi.csc.pid.api.db.FactPiDInterlinkage;
import fi.csc.pid.api.entity.*;
import fi.csc.pid.api.handle.Handle;
import fi.csc.pid.api.model.Sisältö;
import fi.csc.pid.api.model.Syntax;
import fi.csc.pid.api.service.*;
import io.agroal.api.AgroalDataSource;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;
import java.sql.Connection;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.jboss.logging.Logger;

import static fi.csc.pid.api.Util.ACCESSDENIED;
import static fi.csc.pid.api.Util.AD;
import static fi.csc.pid.api.Util.INVALID;
import static fi.csc.pid.api.Util.TODO;
import static fi.csc.pid.api.Util.URLMISSING;
import static fi.csc.pid.api.Util.URLPUUTTUU;
import static fi.csc.pid.api.Util.tarkistaURL;
import static fi.csc.pid.api.handle.Handle.*;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;


@Path("/v1/pid/")
public class PIDResource /*extends PanacheEntityResource<Dim_PID, Long>*/ {

    @Inject
    PIDService ps;
    @Inject
    Fact_pid_interlinkageService fpis;
    @Inject
    Dim_URLService dus;
    @Inject
    Dim_pid_schemeService dpss;
    @Inject
    Fact_touchedService fts;
    @Inject
    AgroalDataSource defaultDataSource;
    @ConfigProperty(name = "surf.port")
    String surf_port;
    @ConfigProperty(name = "gwdg.port")
    String gwdg_port;
    @ConfigProperty(name = "epic.api")
    String epic_api;
     @ConfigProperty(name = "surf.host")
    String surf_host;
    @ConfigProperty(name = "gwdg.host")
    String  gwdg_host;
    @ConfigProperty(name = "gwdg.key")
    String GWDGKEY;
    @ConfigProperty(name = "surf.key")
    String SURFKEY;
     @ConfigProperty(name = "gwdg.user")
    String gwdg_user;
     @ConfigProperty(name = "surf.user")
    String surf_user;
    @ConfigProperty(name = "KEYSTORESS")
    String KEYSTORESS;
    private static final Logger LOG = Logger.getLogger(PIDResource.class);
    private final static String YYYYMM = "YYYYMM";
    private final static String UUID ="UUID";
    private final static String ALAVIIVA = "_";
    public final static String NOCONNECTION = "Can't get connection from database";
    private final static String HANDLEFAULURE = "Handlen luonti epäonnistui";

    /**
     * URN kopioi= tallentaa URN-URL parin kantaan.
     *
     * @param spid String URN
     * @param apikey String secret key
     * @param URL String URL
     * @return String URN (same as spid input)
     */
    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{spid}")
    public Response kopioi(@PathParam("spid") String spid, @HeaderParam("apikey") String apikey, String URL) {
        int id = Util.tarkistaAPIavain(apikey);
        if (INVALID == id) return ACCESSDENIED;
        else {
            String url = tarkistaURL(URL);
            if (url.equals(URLPUUTTUU)) {
                LOG.warn("URL puuttuu!");
                return URLMISSING;
            }
            long duc = dus.countByURL(url);
            if (0 < duc)/*null != ldu && !ldu.isEmpty()) */ {
                LOG.warn("URL olemassa sitä luotaessa: " + url);
            } else { //URLia ei ollut jo olemassa
                int scheme = ApplicationLifecycle.scheme(id, "URN");
                return kopioi(spid, url, scheme,id);
            }
            // ylempänä on jo log.warn, jos tänne päädytään
            return Response.status(501, "Päädyttiin aivan väärään paikkaan, URL olemassa?").build();
        }
    }

    /**
     * DOI kopioi= tallentaa DOI-URL parin kantaan.
     *
     * @param prefix String DOI prefix 10.2
     * @param suffix String DOI suffix
     * @param apikey String secret
     * @param URL String {'URL': 'https.//...'}
     * @return String DOI = prefix + suffix
     */
    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{prefix}/{suffix}") //bacause DOI, prefix is allways starts with 10
    public Response kopioi(@PathParam("prefix") String prefix,
                           @PathParam("suffix") String suffix,
                           @HeaderParam("apikey") String apikey,
                           String URL) {
        int id = Util.tarkistaAPIavain(apikey);
        if (INVALID == id) return ACCESSDENIED;
        if ((null == prefix) || (null == suffix))
            return Response.status(400, "The pathparam must be 10/suffix").build();
        if (!prefix.startsWith("10")) //10.82614/...
            return Response.status(400, "This is DOI API: the prefix must start with 10").build();
        final String DOI = prefix+"/"+suffix;
        final String url = tarkistaURL(URL);
        if (url.equals(URLPUUTTUU))
            return URLMISSING;
        long duc = dus.countByURL(url);
        if (0 < duc)
            return Response.status(400, "URL already exists!").build();
        int scheme = ApplicationLifecycle.scheme(id, "DOI");
        return kopioi(DOI, url, scheme,id);
    }

    /**
     * ListHistory
     *
     * @param pid    tunniste, jolle käsittelyhistoria tulostetaan
     * @param apikey String Palvelun apiavoin
     * @return JSON history data
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{pid}")
    public Response listHistory(@PathParam("pid") Long pid, @HeaderParam("apikey") String apikey) {
        int sid = Util.tarkistaAPIavain(apikey);
        if (INVALID == sid) return ACCESSDENIED;
        else {
            Dim_PID dp = ps.getById(pid);
            if (null == dp) return Response.status(404, "PID don't exist.").build();
            else {
                List<Fact_touched> lft = fts.getById(dp.internal_id);
                if ((null != lft) && (!lft.isEmpty())) {
                    return Response.ok(lft).build();
                }
            }
            return Response.ok(ps.getById(pid)).build();
        }
    }

    /**
     * Päivittää uuden URLin olemassa olevaan PIDiin
     *
      * @param id long PIDs database id
     * @param apikey String Palvelun apiavoin
     * @param URL String
     * @return JSON 200 ok with new URL
     */
    @Transactional
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response update(@PathParam("id") Long id, @HeaderParam("apikey") String apikey, String URL) {
        int sid = Util.tarkistaAPIavain(apikey);
        if (INVALID == sid) return ACCESSDENIED;
        else {
            Dim_PID pid = ps.getById(id);
            if (null == pid) {
                return Response.notModified("PID do not exist!").build();
            } else {
                String url = tarkistaURL(URL);
                if (url.equals(URLPUUTTUU)) {
                     return URLMISSING;
                }
                if (pid.dim_serviceid == sid) {
                    int urlid = fpis.getById(id).getUrlId();
                    Dim_url du = dus.getById(urlid);
                    historiantallennus(sid, id, du.url);
                    du.url = url;
                    du.persistAndFlush();
                    return Response.ok(du).build();
                } else {
                    return Response.status(AD, "You are NOT URL owner!").build();
                }
            }

        }
    }

    /**
     * Create new PID
     *
     * @param apikey String Palvelun apiavoin
     * @param s      Syöte contains URL, type like URN, persisti
     * @return String "text/plain" new PID (or a error message)
     */
    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response add(@HeaderParam("apikey") String apikey, Syöte s) {
        int id = Util.tarkistaAPIavain(apikey);
        if (INVALID == id) return ACCESSDENIED; //full stop

        if (null == s.getUrl()) {
            LOG.warn("URL puuttuu! " + s.type);
            return URLMISSING;
        }
        long duc = dus.countByURL(s.getUrl());
       if (0 < duc) {
            LOG.warn("URL olemassa sitä luotaessa");
            //return Response.status(500, "URL already exist").build(); //putoaa lopun TODOhun
        } else { //URLia ei ollut jo olemassa
            int scheme = ApplicationLifecycle.scheme(id, s.type);
            if (scheme < 0) {
                LOG.warn("scheme missing: " + id + s.type);
                return Response.status(500, "scheme missing:" + scheme).build();
            }
            Syntax alut = syntax(scheme, id, s.type);
            try {
                Dim_pid_scheme dps = dpss.getById(scheme);
                String pidSyntaxRegexp = dps.pid_syntax_regexp;
                Connection connection = defaultDataSource.getConnection();
                Util util = new Util();
                Sisältö content = util.realCreate(id, scheme, connection);
                if (content.getStatus() < 0) {
                    return Response.status(500, content.getError()).build();
                }
                Dim_PID pid = content.getPid();
                StringBuffer sb = content.getSb();
                if (pidSyntaxRegexp.contains("UUID")) {
                    String sisältö = content.getTarkistettava() + content.getTarkiste();
                    sb = new StringBuffer(Util.UUID(sisältö));
                }
                if (alut.alaviiva()) {
                    sb.insert(6, ALAVIIVA);
                }
                if (s.type.equals("Handle"))
                    sb.insert(0, alut.handle());
                else
                    sb.insert(0, alut.urn());
                if (alut.alaviiva()) {
                    sb.append(ALAVIIVA);
                }
                if (!pidSyntaxRegexp.contains("UUID")) { //NOT
                    sb.append(content.getTarkiste());
                }
                Dim_CSC_info dci = new Dim_CSC_info(sb.substring(11));//urn:nbn:fi:
                dci.setChecksum(content.getTarkiste());
                dci.persistAndFlush();
                pid.identifier_string = sb.toString();

                Dim_url url = new Dim_url();
                url.url = s.getUrl();
                url.persistAndFlush();
                Dim_PID dph = null;
                if (alut.ishandle()) {
                    String host = util.surforGWDG(id, surf_host, gwdg_host );
                    String port = util.surforGWDG(id, surf_port, gwdg_port );
                    String key = util.surforGWDG(id, SURFKEY, GWDGKEY );
                    String user =  util.surforGWDG(id, surf_user, gwdg_user );
                    String prefix = util.surforGWDG(id, SURF_SERVICE_PREFIX, GWDG_SERVICE_PREFIX);
                    String loppu = util.surforGWDG(id, JSONSURF,  JSONGWDG);
                    Handle h = new Handle(host, port, epic_api, user, key, KEYSTORESS);
                    if (!h.create(s.getUrl(), prefix, content.getTarkistettava() + content.getTarkiste(), loppu)) {
                        LOG.error(HANDLEFAULURE);
                        return Response.status(500, HANDLEFAULURE).build();
                    }
                    // kirjoitetaan handle kantaan, JOS URN&Handle
                    if (s.type.equals("URN&Handle")) {
                        Sisältö handle = util.realCreate(id, scheme, connection);
                        dph = handle.getPid();
                        //URN ja Handle käyttävät samaa suffiksia! joten niitä voi (ja täyttyy!!!) miksata
                        dph.identifier_string = alut.handle() + content.getTarkistettava() + content.getTarkiste();
                        dph.update(connection);
                        FactPiDInterlinkage fpi = new FactPiDInterlinkage(handle.getId(), url.getId(), dci.getPIDMiSe_suffix());
                        fpi.tallenna(connection);
                        LOG.info("Handle " + dph.identifier_string + "tallennettiin kantaan " + dph.internal_id);
                    } //huomaa että URL ja Dim_CSC_info ovat yhteiset handlelle ja URNille!
                }
                historiantallennus(id, content.getId(), s.getUrl()); //content.getId() = internal_id

                LOG.info("URL " + url.getId() + " ja PID tallenetaan kantaan");
                pid.update(connection);
                FactPiDInterlinkage fpi = new FactPiDInterlinkage(content.getId(), url.getId(), dci.getPIDMiSe_suffix());
                fpi.tallenna(connection);
                if (alut.ishandle() && !(null == dph)) {
                    return Response.ok(dph.identifier_string).build();
                } else {
                    return Response.ok(pid.identifier_string).build();
                }
            } catch (java.sql.SQLException e) {
                LOG.error(NOCONNECTION);
                return Response.status(501, NOCONNECTION).build();
            }
       }
        return TODO;

    }


    /**
     * Construct String prefix of the PID like urn:nbn:fi:oerfi-202010 based too simple parsing
     * of the pidSyntaxRegexp
     *
     * @param scheme int index of dim_pid_scheme in database
     * @return String prefix of the PID like urn:nbn:fi:oerfi-202010
     */
    private Syntax syntax(int scheme, int id, String type) {
        Dim_pid_scheme dps = dpss.getById(scheme);
        String pidSyntaxRegexp = dps.pid_syntax_regexp;
        boolean alaviiva = dps.pid_syntax_regexp.contains(ALAVIIVA);
        boolean ishandle = type.contains("Handle");
        int index = -11;
        if (pidSyntaxRegexp.contains(YYYYMM)) {
            index = pidSyntaxRegexp.indexOf(YYYYMM); //"YYYYMM" not very clever
        } else {
            index = pidSyntaxRegexp.indexOf(UUID);
        }
        if (index < 0) {
            LOG.error("Tietokannassa liian vaikea Regexp, please fix the code");
            return Syntax.virhe(alaviiva, false, ishandle);
        }
        String handle = null;
        if (ishandle) {
            handle = pidSyntaxRegexp.substring(0, index);
            int urnscheme = ApplicationLifecycle.scheme(id, type);
            if (type.contains("URN")) //URN&Handle
                urnscheme = ApplicationLifecycle.scheme(id, "URN");
            if (urnscheme < 0) {
                LOG.warn("urn scheme missing: " + id + type);
                return Syntax.virhe(alaviiva, false, ishandle);
            }
            Dim_pid_scheme urndps = dpss.getById(urnscheme);
            String urnpidSyntaxRegexp = urndps.pid_syntax_regexp;
            int urnindex = urnpidSyntaxRegexp.indexOf(YYYYMM);
            if (urnindex < 0) {
                LOG.error("Tietokannassa liian vaikea URN Regexp, please fix the code");
                return Syntax.virhe(alaviiva, false, ishandle);
            }
            return new Syntax(alaviiva, true, ishandle, urnpidSyntaxRegexp.substring(0, urnindex), handle);
        } else
            return new Syntax(alaviiva, true, ishandle, pidSyntaxRegexp.substring(0, index), handle);
    }

    /**
     * Save the Fact_touched table;
     * @param id int service id
     * @param internal_id long dim_PIDinternal_id
     * @param url String URL
     */
    public static void historiantallennus(int id, long internal_id, String url) {
        Fact_touched ft = new Fact_touched();
        ft.dim_organizationid =ApplicationLifecycle.serviceorganization.get(id);
        ft.dim_PIDinternal_id =internal_id;
        ft.dim_serviceid =id;
        ft.old_values = url;
        ft.timestamp =new Timestamp(System.currentTimeMillis());
        ft.persistAndFlush();
    }

    Response kopioi(String spid, String url, int scheme, int id) {
        if (scheme < 0) {
            LOG.warn("URN scheme missing: " + id);
            return Response.status(500, "scheme missing:" + scheme).build();
        }
        Dim_PID pid = new Dim_PID();
        pid.dim_serviceid = id;
        pid.dim_pid_scheme_id = scheme;
        if (null == pid.created) {
            pid.created = LocalDateTime.now();
        }
        try {
            Connection connection = defaultDataSource.getConnection();
            long internal_id = pid.tallenna(connection);
            if (internal_id < 0) {
                return Response.status(500, "Virhe sarjanumeron luonnissa").build();
            }
            Dim_CSC_info dci = new Dim_CSC_info(spid);//urn:nbn:fi:
            dci.persistAndFlush();
            pid.identifier_string = spid;

            Dim_url durl = new Dim_url();
            durl.url = url;
            durl.persistAndFlush();

            historiantallennus(id, internal_id, url);

            LOG.info("URL " + durl.getId() + " ja PID tallenetaan kantaan");
            pid.update(connection);
            FactPiDInterlinkage fpi = new FactPiDInterlinkage(internal_id, durl.getId(), dci.getPIDMiSe_suffix());
            fpi.tallenna(connection);
            return Response.ok(pid.identifier_string).build();
        } catch (java.sql.SQLException e) {
            LOG.error(NOCONNECTION);
            return Response.status(501, NOCONNECTION).build();
        }
    }
}
