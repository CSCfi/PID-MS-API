package fi.csc.pid.api.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jboss.logging.Logger;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Entity
@Table(name = "dim_url")
public class Dim_url extends PanacheEntityBase {

    private static final String INSERT = "INSERT INTO dim_url (url) VALUES (?)";
    private static final Logger LOG = Logger.getLogger(Dim_url.class);

    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id // @GeneratedValue johtaa java.sql.SQLException: Table 'piddev.hibernate_sequence' doesn't exist
    public int id;
    public String url;

    public Dim_url() {
        //tarkoituksella tyhjä: "a default constructor is required by the JSON serialization layer"
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int talleta(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, url);
            int tulos = statement.executeUpdate();
            ResultSet tableKeys = statement.getGeneratedKeys();
            if (1 == tulos) {
                tableKeys.next();
                id = tableKeys.getInt(1);
                statement.close();
                return id;
            } else {
                LOG.warn("Database write dim url return: " + tulos);
                statement.close();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }
}