package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.jboss.logging.Logger;

@Entity
@Table(name = "br_dim_service_dim_pid_scheme")
public class Br_dim_service_dim_pid_scheme extends PanacheEntityBase {

    private static final String INSERT = "INSERT INTO br_dim_service_dim_pid_scheme VALUES (?,?)";
    private static final Logger LOG = Logger.getLogger(Dim_url.class);
    int dim_service_id;
    @Id
    int dim_pid_scheme_id;

    public int getService_id() {
        return dim_service_id;
    }
    public int getScheme_id() {
        return dim_pid_scheme_id;
    }

    public void setDim_service_id(int dim_service_id) {
        this.dim_service_id = dim_service_id;
    }

    public void setDim_pid_scheme_id(int dim_pid_scheme_id) {
        this.dim_pid_scheme_id = dim_pid_scheme_id;
    }

    public void talleta(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, dim_service_id);
            statement.setInt(2,dim_pid_scheme_id);
            int tulos = statement.executeUpdate();
            if (1 == tulos) {
                statement.close();
            } else {
                LOG.warn("Database  return: br_dim_service_dim_pid_scheme" + tulos);
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
