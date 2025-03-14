package fi.csc.pid.api.handle;

import fi.csc.pid.api.Util;
import fi.csc.pid.api.doi.DOIResource;
import fi.csc.pid.api.entity.Dim_url;
import fi.csc.pid.api.service.Dim_URLService;
import fi.csc.pid.api.service.Fact_pid_interlinkageService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.HeaderParam;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import jakarta.inject.Inject;

import static fi.csc.pid.api.PIDResource.historiantallennus;
import static fi.csc.pid.api.Util.ACCESSDENIED;
import static fi.csc.pid.api.Util.INVALID;
import static fi.csc.pid.api.Util.URLMISSING;
import static fi.csc.pid.api.Util.URLPUUTTUU;
import static fi.csc.pid.api.Util.tarkistaURL;
import static fi.csc.pid.api.doi.DOI.uriFromString;

import fi.csc.pid.api.handle.Handle;

import static fi.csc.pid.api.handle.Handle.*;
import static fi.csc.pid.api.handle.TLS.getSslParam;


@Path("/v1/pid/handle/")
public class HandleResource {

    @ConfigProperty(name = "surf.host")
    String surf_host;
    @ConfigProperty(name = "gwdg.host")
    String  gwdg_host;
    @ConfigProperty(name = "surf.port")
    String surf_port;
    @ConfigProperty(name = "gwdg.port")
    String gwdg_port;
    @ConfigProperty(name = "epic.api")
    String epic_api;
    @ConfigProperty(name = "surf.key")
    String SURFKEY;
    @ConfigProperty(name = "surf.user")
    String surf_user;
    @ConfigProperty(name = "gwdg.key")
    String GWDGKEY;
    @ConfigProperty(name = "gwdg.user")
    String gwdg_user;
    @ConfigProperty(name = "KEYSTORESS")
    String KEYSTORESS;
    @Inject
    Fact_pid_interlinkageService fpis;
    @Inject
    Dim_URLService dus;

    private static final Logger LOG = Logger.getLogger(DOIResource.class);
    public static final int EOSC = 5;


    @Transactional
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{prefix}/{suffix}")
    public Response update(@HeaderParam("apikey") String apikey, @PathParam("prefix") String prefix,
                           @PathParam("suffix") String suffix, String URL) {
        int sid = Util.tarkistaAPIavain(apikey);
        if (INVALID == sid) return ACCESSDENIED; //full stop

        if ((null == prefix) || (null == suffix))
            return Response.status(400, "The pathparam must be prefix/suffix").build();
        final String handle = prefix+"/"+suffix;
        String url = tarkistaURL(URL);
        if (url.equals(URLPUUTTUU)) {
            return URLMISSING;
        }
        // Syöte tarkistettu (Handlen API vastaa prefix/suffixtarkistuksista!)
        Util util = new Util();
        String loppu = util.surforGWDG(sid, JSONSURF,  JSONGWDG);
        String json = Handle.json(url, loppu);
        String host = util.surforGWDG(sid, surf_host, gwdg_host );
        String port = util.surforGWDG(sid, surf_port, gwdg_port );
        String key = util.surforGWDG(sid, SURFKEY, GWDGKEY );
        String user =  util.surforGWDG(sid, surf_user, gwdg_user );
        URI uri = uriFromString(EPIC_PROTOCOL+host+":"+port+epic_api+handle);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Handle clientCert=\"true\"")
                .header("Content-Lengt", String.valueOf(json.length()))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "");
        byte[]  privateData= Base64.getDecoder().decode(privateKeyPEM);
        TLS tls = new TLS(KEYSTORESS);
        HttpClient client = HttpClient.newBuilder()
                .sslContext(tls.getSSLContext(host, user, privateData))
                .sslParameters(getSslParam())
                .build();
        HttpResponse<String> response;
        int statusCode = -1;
        String body = "";
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            body = response.body();
            System.out.println(statusCode + body);
            if (päivitäTietokanta(prefix, suffix, url, sid))
                return Response.ok(url).build();
            else
                return Response.status(512, "Handle päivittyi, mutta tietokanta ei: ota yhteys ylläpitoon").build();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        return Response.status(statusCode, body).build();
    }

    private boolean päivitäTietokanta(String prefix, String suffix, String url, int sid) {
        int id = parsiID(suffix);
        int urlid = fpis.getById(id).getUrlId();
        Dim_url du = dus.getById(urlid);
        historiantallennus(sid, id, du.url);
        du.url = url;
        du.persistAndFlush();
        return true;
    }

    private int parsiID(String s) {
        String lyhyt = s.substring(9, s.length() - 1);
        return  Integer.parseInt(lyhyt);
        //String numberString = s.replaceAll("[^0-9]", "");
    }
}
