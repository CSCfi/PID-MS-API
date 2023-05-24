package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.logging.Logger;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Entity
@Table(name = "dim_pid_scheme")
public class Dim_pid_scheme extends PanacheEntityBase {
    @Id
    public int id;
    public String pid_type;
    public String landing_page;
    public String pid_syntax_regexp;

    private static final String INSERT = "INSERT INTO dim_pid_scheme (pid_type, landing_page, pid_syntax_regexp) VALUES (?,?,?)";
    private static final Logger LOG = Logger.getLogger(Dim_url.class);

    public int talleta(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pid_type);
            statement.setString(2,landing_page);
            statement.setString(3, pid_syntax_regexp );
            int tulos = statement.executeUpdate();
            ResultSet tableKeys = statement.getGeneratedKeys();
            if (1 == tulos) {
                tableKeys.next();
                id = tableKeys.getInt(1);
                statement.close();
                return id;
            } else {
                LOG.warn("Database write dim_pid_scheme return: " + tulos);
                statement.close();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }
}
