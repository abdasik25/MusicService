package by.epam.onemusic.pool;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ConnectionPool instance;
    private BlockingDeque<ProxyConnection> freeConnections;
    private BlockingDeque<ProxyConnection> usedConnections;
    private static ReentrantLock reentrantLock = new ReentrantLock();
    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    private DBPropertiesHandler dbPropertiesHandler = DBPropertiesHandler.getInstance();

    private ConnectionPool() {
        init();
    }

    private void init() {
        int dequeSize = dbPropertiesHandler.getDbPoolSize();
        freeConnections = new LinkedBlockingDeque<>(dequeSize);
        usedConnections = new LinkedBlockingDeque<>(dequeSize);
        for (int i = 0; i < dequeSize; i++) {
            try {
                freeConnections.add(ConnectionPool.createConnection());
            } catch (SQLException e) {
                LOGGER.error("Error while adding to connections pool", e);
                e.printStackTrace();
            }
        }
        if (freeConnections.getFirst() == null) {
            LOGGER.fatal("Connection pool is empty.");
            throw new ExceptionInInitializerError("Connection pool is empty.");
            //TODO STOP APPLICATION
        }
    }

    private static ProxyConnection createConnection() throws SQLException {
        DBPropertiesHandler dbPropertiesHandler = DBPropertiesHandler.getInstance();
        Connection connection = DriverManager.getConnection(
                dbPropertiesHandler.getConnectionString(),
                dbPropertiesHandler.getDbUser(),
                dbPropertiesHandler.getDbPass());
        ProxyConnection proxyConnection = new ProxyConnection(connection);
        return proxyConnection;
    }

    public static ConnectionPool getInstance() {
        if (!isCreated.get()) {
            try {
                reentrantLock.lock();
                if (instance == null) {
                    instance = new ConnectionPool();
                    isCreated.set(true);
                    LOGGER.info("Connection pool was created.");
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        return instance;
    }

    public Connection takeConnection() {
        ProxyConnection connection = null;
        try {
            connection = freeConnections.take();
            usedConnections.put(connection);
            LOGGER.info("Connection was taken.");
        } catch (InterruptedException e) {
            LOGGER.error("Connection leak.");
            Thread.currentThread().interrupt();
        }
        return connection;
    }

    public boolean releaseConnection(Connection connection) {
        if (connection instanceof ProxyConnection) {
            ProxyConnection tmp = (ProxyConnection) connection;
            try {
                if (!tmp.getAutoCommit()) {
                    tmp.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("Error while setting autocommit");
            }
            usedConnections.remove(connection);
            freeConnections.offer(tmp);
            LOGGER.info("Connection was released.");
            return true;
        } else {
            return false;
        }
    }

    //2 метода

    //удалит все коннекшн из одной очереди (avaliable connections) private method maybe в каких местах потом узнают вызывать
    public void closePool() throws InterruptedException, SQLException {
        for (int i = 0; i < dbPropertiesHandler.getDbPoolSize(); i++) {
            ProxyConnection connection = freeConnections.take();
            connection.realClose();
        }
    }

//    void registerDrivers() {
//    }
//
//    deregisterDriver через лямбду, закрыть драйвера нужно все, кажется что создается только один
//    public void deregisterDrivers() {
//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//        while (drivers.hasMoreElements()) {
//            Driver driver = drivers.nextElement();
//            try {
//                DriverManager.deregisterDriver(driver);
//            } catch (SQLException e) {
//                //TODO LOG
//                e.printStackTrace();
//            }
//        }
//    }
}
