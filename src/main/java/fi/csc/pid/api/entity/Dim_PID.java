package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import jakarta.persistence.*;
import java.sql.*;
import java.time.LocalDateTime;
import org.jboss.logging.Logger;

@RegisterForReflection
@Entity
@Table(name = "dim_PID")
public class Dim_PID extends PanacheEntityBase {
    private static final String INSERT = "INSERT INTO dim_PID (dim_serviceid, dim_pid_scheme_id, " +
            " identifier_string, created) VALUES (?, ?, ?, ?)";
     private static final String UPDATE = "UPDATE dim_PID SET identifier_string = ? WHERE internal_id=?";

    private static final Logger LOG = Logger.getLogger(Dim_PID.class);

    @Column(name = "internal_id", unique = true, nullable = false)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long internal_id;
    public int dim_serviceid;
    public int dim_pid_scheme_id;
    public String identifier_string;
    public LocalDateTime created;

    public Dim_PID() {
        //tarkoituksella tyhjä: "a default constructor is required by the JSON serialization layer"
    }

  public long tallenna(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, dim_serviceid);
            statement.setInt(2, dim_pid_scheme_id);
            statement.setString(3, identifier_string);
            statement.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
            int tulos = statement.executeUpdate();
            ResultSet tableKeys = statement.getGeneratedKeys();
            if (1 == tulos) {
                tableKeys.next();
                 internal_id = tableKeys.getInt(1);
                 statement.close();
                return internal_id;
            } else {
                LOG.warn("Database write return: "+tulos);
                statement.close();
                return -1;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return -2;
        }
    }

    public boolean update(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(UPDATE);
            statement.setString(1, identifier_string);
            statement.setLong(2, internal_id);
            int tulos = statement.executeUpdate();
            //connection.commit();
            statement.close();
            if (1 == tulos) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return false;
    }
}
