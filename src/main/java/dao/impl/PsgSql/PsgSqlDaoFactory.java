package dao.impl.PsgSql;

import dao.DAOFactory;
import dao.SingerDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Mayer Roman on 10.06.2016.
 */
public class PsgSqlDaoFactory implements DAOFactory {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "system";

    private static final Logger LOGGER = LogManager.getLogger(PsgSqlDaoFactory.class);

    private Connection connection;

    public PsgSqlDaoFactory() {

        try {
            DriverManager.registerDriver((Driver)
                    Class.forName("org.postgresql.Driver").newInstance());


            Properties properties = new Properties();
            properties.setProperty("user", USER);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("ssl", "false");

            connection = DriverManager.getConnection(URL, properties);

        } catch (SQLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            LOGGER.error("Getting connection do PsqSqlDB exception", e);
        }
    }

    @Override
    public SingerDAO getSingerDAO() {
        return new PsgSqlSingerDAO(connection);
    }
}
