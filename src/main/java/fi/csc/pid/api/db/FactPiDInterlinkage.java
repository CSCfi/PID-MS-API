package fi.csc.pid.api.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jboss.logging.Logger;

public class FactPiDInterlinkage {

    private static final String INSERT = "INSERT INTO fact_pid_interlinkage VALUES (?, ?, ?)";

    private static final Logger LOG = Logger.getLogger(FactPiDInterlinkage.class);

    public long dim_PIDinternal_id;
    public int url_id;
    public String dim_CSC_infoPIDMiSe_suffix;

    public FactPiDInterlinkage(long dim_PIDinternal_id, int url_id, String suffix) {
        this.dim_PIDinternal_id = dim_PIDinternal_id;
        this.url_id = url_id;
        this.dim_CSC_infoPIDMiSe_suffix = suffix;
    }

    /**
     * Because hibernate can't save this (or  Fact_pid_interlinkage actually)
     *
     * @param connection java.sql.connection or from some connection pool
     * @return boolean true if statement.executeUpdate() return 1 (line inserted)
     */
    public boolean tallenna(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT);
            statement.setLong(3, dim_PIDinternal_id);
            statement.setInt(2, url_id);
            statement.setString(1, dim_CSC_infoPIDMiSe_suffix);
            int tulos = statement.executeUpdate();
            statement.close();
            //connection.close();
            if (1 == tulos) {
                return true;
            } else {
                LOG.warn("Database write return: "+tulos);
                return false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        //return true;
    }

}
