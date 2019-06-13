package by.epam.onemusic.pool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

class DBPropertiesHandler {

    private String dbDriver;
    private String dbPort;
    private String dbHost;
    private String dbUser;
    private String dbPass;
    private String dbName;
    private String dbEncoding;
    private int dbPoolSize;
    private boolean dbUseUnicode;
    private static DBPropertiesHandler instance;
    private String connectionString;

    private Properties properties;

    private DBPropertiesHandler() {
        init();
    }

    private void init() {

        properties = new Properties();

        try (FileInputStream inputStream = new FileInputStream("properties/db.properties")) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dbDriver = properties.getProperty("jdbc.driver");
        dbName = properties.getProperty("jdbc.name");
        dbPass = properties.getProperty("jdbc.password");
        dbHost = properties.getProperty("jdbc.host");
        dbUser = properties.getProperty("jdbc.username");
        dbPort = properties.getProperty("jdbc.port");
        dbPoolSize = Integer.parseInt(properties.getProperty("jdbc.poolsize"));
        dbUseUnicode = Boolean.parseBoolean(properties.getProperty("jdbc.useUnicode"));
        dbEncoding = properties.getProperty("jdbc.encoding");
        connectionString = "jdbc:mysql://" + dbName + ":"
                + dbPort + "/" + dbName +
                "?verifyServerCertificate=false" +
                "&useSSL=false" +
                "&requireSSL=false" +
                "&useLegacyDatetimeCode=false" +
                "&amp" +
                "&serverTimezone=UTC";
    }

    static DBPropertiesHandler getInstance() {
        if (instance == null) {
            instance = new DBPropertiesHandler();
        }
        return instance;
    }

    String getDbDriver() {
        return dbDriver;
    }

    String getDbPort() {
        return dbPort;
    }

    String getDbHost() {
        return dbHost;
    }

    String getDbUser() {
        return dbUser;
    }

    String getDbPass() {
        return dbPass;
    }

    String getDbName() {
        return dbName;
    }

    String getDbEncoding() {
        return dbEncoding;
    }

    int getDbPoolSize() {
        return dbPoolSize;
    }

    boolean isDbUseUnicode() {
        return dbUseUnicode;
    }

    Properties getProperties() {
        return properties;
    }

    String getConnectionString() {
        return connectionString;
    }
}
