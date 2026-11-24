package agentie.cfr.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexiuneDB {

    private static final String FISIER_CONFIG = "db.properties";

    private static Connection conexiune;

    private ConexiuneDB() {
    }

    public static Connection getConnection() throws SQLException {
        if (conexiune == null || conexiune.isClosed()) {
            Properties config = incarcaConfig();
            conexiune = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("user"),
                    config.getProperty("password"));
        }
        return conexiune;
    }

    private static Properties incarcaConfig() throws SQLException {
        Path cale = Paths.get(FISIER_CONFIG);
        if (!Files.exists(cale)) {
            throw new SQLException("Lipseste fisierul " + FISIER_CONFIG
                    + " (copiaza db.properties.example si completeaza-l)");
        }
        Properties config = new Properties();
        try (InputStream in = Files.newInputStream(cale)) {
            config.load(in);
        } catch (IOException e) {
            throw new SQLException("Nu pot citi " + FISIER_CONFIG, e);
        }
        return config;
    }

    public static void closeConnection() {
        if (conexiune != null) {
            try {
                conexiune.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
