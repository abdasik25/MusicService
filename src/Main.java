//import by.epam.onemusic.pool.DBPropertiesHandler;

import by.epam.onemusic.pool.ConnectionPool;

public class Main {

    public static void main(String[] args) {
//        DBPropertiesHandler dbPropertiesHandler = DBPropertiesHandler.getInstance();
//        String connectionString = "jdbc:mysql://" + dbPropertiesHandler.getDbHost() + ":"
//                + dbPropertiesHandler.getDbPort() + "/" + dbPropertiesHandler.getDbName() +
//                "?verifyServerCertificate=false" +
//                "&useSSL=false" +
//                "&requireSSL=false" +
//                "&useLegacyDatetimeCode=false" +
//                "&amp" +
//                "&serverTimezone=UTC";
//        try {
//            Connection connection = DriverManager.getConnection(connectionString,
//                    dbPropertiesHandler.getDbUser(),
//                    dbPropertiesHandler.getDbPass());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        ConnectionPool.getInstance().takeConnection();
    }
}
