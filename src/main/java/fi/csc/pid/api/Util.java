package fi.csc.pid.api;

import fi.csc.pid.api.entity.Dim_PID;
import fi.csc.pid.api.model.Sisältö;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.json.JSONObject;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Objects;

public class Util {
    public final static int INVALID = -1;
    public final static int AD = 403; //Forbidden
    private final static String KEYERROR = "API key was INVALID";
    public final static Response ACCESSDENIED = Response.status(AD, KEYERROR).build();
    public final static Response TODO = Response.status(501, "Not yet implemented").build();
    public final static String URLPUUTTUU = "URLPUUTTUU";
    public final static Response URLMISSING = Response.status(400, "URL must be JSON olio {\"URL\": \"arvo\"}!").build();
    private static final Logger LOG = Logger.getLogger(Util.class);

    private static final DecimalFormat df = new DecimalFormat("00000000");
    private static final DecimalFormat dfm = new DecimalFormat("00"); //kuukausi
    private static final FieldPosition fp = new FieldPosition(NumberFormat.INTEGER_FIELD);

    /**
     * Check that apikey is in dim_service table
     *
     * @param apikey String very long secret random String. Each service have own.
     * @return int service Id or -1 = INVALID
     */
    public static int tarkistaAPIavain(String apikey) {
        try {
            Integer id = ApplicationLifecycle.apikeyservice.get(apikey);
            return Objects.requireNonNullElse(id, INVALID);
        } catch (java.lang.NullPointerException e) {
            LOG.warn("NullPointerException: " + apikey);
            return INVALID;
        }
    }

    public static String tarkistaURL(String URL) {
        if (null == URL) {
            LOG.warn("URL puuttuu!");
            return URLPUUTTUU;
        }
        JSONObject jo = new JSONObject(URL);
        String url = jo.getString("URL");
        if (null == url) {
            LOG.warn("Syötteen pitäisi olla JSON olio {URL: arvo}!");
            return URLPUUTTUU;
        }
        return url;
    }
    /**
     * Luo pysyvän tunnisteen tietokantaan
     *
     * @param serviceid int dimservice-table index
     * @param scheme int dim_pid_scheme index
     * @param connection Connection to database
     * @return Sisältö Full pid components (URN or Handle)
     */
    public Sisältö realCreate(int serviceid, int scheme, Connection connection) {
        Dim_PID pid = new Dim_PID();
        pid.dim_serviceid = serviceid;
        pid.dim_pid_scheme_id = scheme;
        if (null == pid.created) {
            //LOG.warn("Voisiko päivämäärän myös alustaa tietokannan puolella PIDiä luodessa");
            pid.created = LocalDateTime.now();
        }
        long internal_id = pid.tallenna(connection);
        if (internal_id < 0) {
            return new Sisältö(-1, "Virhe sarjanumeron luonnissa");
        }
        StringBuffer sb = new StringBuffer();
        sb.append(pid.created.getYear());
        dfm.format(pid.created.getMonthValue(), sb, fp);
        df.format(internal_id, sb, fp);
        String tarkistettava = sb.toString();
        int tarkiste = luhn(tarkistettava);
        if (tarkiste < 0) {
            return new Sisältö(-2, "Virhe tarkisteen laskennassa." + sb);
        }
        return new Sisältö(0, null, pid, sb, internal_id, tarkistettava, tarkiste);
    }

    /**
     * Algoritmi tarkistusnumeron laskemiseksi. Käytössä luottokorteissa ym.
     *
     * @param str String numeroita (String on tyhmä, koska kohta muutetaan numeroiksi
     * @return int yksi numero joka riippuu koko syötteestä
     */
    private int luhn(String str) {
        int[] ints = new int[str.length()];
        try {
            for (int i = 0; i < str.length(); i++) { //tämän voinee optimoida pois ja antaa numeroita syötteenä
                ints[i] = Integer.parseInt(str.substring(i, i + 1));
            }
        } catch (NumberFormatException e) {
            LOG.error(str + " sisältää ei numeromerkkejä");
            LOG.error(e.getMessage());
            return -1;
        }
        for (int i = ints.length - 2; i >= 0; i = i - 2) {//huomaa vähennys kahdella!!!
            int j = ints[i];
            j = j * 2;
            if (j > 9) {
                j = j % 10 + 1;
            }
            ints[i] = j;
        }
        int sum = 0;
        for (int anInt : ints) {
            sum += anInt;
        }
        if (sum % 10 == 0) {
            return 0;
        } else return 10 - (sum % 10);
    }


    /**
     * Yhdistää kaksi byte[]
     *
     * @param alku byte[]
     * @param loppu byte[]
     * @return byte[] alku+loppu
     */
    public static byte[] joinBytes(byte[] alku, byte[] loppu) {
        final int finalLength = alku.length + loppu.length;
        final byte[] result = new byte[finalLength];

        System.arraycopy(alku, 0, result, 0, alku.length);
        System.arraycopy(loppu, 0, result, alku.length, loppu.length);
        return result;
    }

}
