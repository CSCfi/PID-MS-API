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
@Table(name = "dim_service")
public class Dim_service extends PanacheEntityBase {
    @Id
    public int id;
    public String name;
    public int url_id;
    public String apikey;

    private static final String INSERT = "INSERT INTO dim_service (name, url_id, apikey) VALUES (?, ?, ?)";
    private static final Logger LOG = Logger.getLogger(Dim_service.class);

    public String getApikey(){
        return apikey;
    }

    public int getId() {
        return id;
    }


    public int talleta(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setInt(2,url_id);
            statement.setString(3, apikey);
            int tulos = statement.executeUpdate();
            ResultSet tableKeys = statement.getGeneratedKeys();
            if (1 == tulos) {
                tableKeys.next();
                id = tableKeys.getInt(1);
                statement.close();
                return id;
            } else {
                LOG.warn("Database write dim service return: " + tulos);
                statement.close();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -2;
        }
    }
}
