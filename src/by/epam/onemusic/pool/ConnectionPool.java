/**
 * Created by Alexander Lomat on 15.05.19
 * version 0.0.3
 */

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
    private DBPropertiesHandler dbPropertiesHandler;

    private ConnectionPool() {
        init();
    }

    private void init() {
        dbPropertiesHandler = DBPropertiesHandler.getInstance();
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

    public ProxyConnection takeConnection() {
        ProxyConnection connection = null;
        try {
            connection = freeConnections.take();
            usedConnections.put(connection);
            int size = freeConnections.size();
            LOGGER.info("Connection was taken. There are " + size + " free connections.");
        } catch (InterruptedException e) {
            LOGGER.error("Connection was interrupted.");
            Thread.currentThread().interrupt();
        }
        return connection;
    }

    public void releaseConnection(Connection connection) {
        if (connection instanceof ProxyConnection) {
            ProxyConnection tmp = (ProxyConnection) connection;
            try {
                if (!tmp.getAutoCommit()) {
                    tmp.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("Error while setting autocommit.");
            }
            usedConnections.remove(connection);
            freeConnections.offer(tmp);
            int size = usedConnections.size();
            LOGGER.info("Connection was released. Remaining " + size + " working connections.");
        } else {
            LOGGER.info("Connection was not released.");
        }
    }

    public void closePool() throws InterruptedException, SQLException {
        for (int i = 0; i < dbPropertiesHandler.getDbPoolSize(); i++) {
            ProxyConnection connection = freeConnections.take();
            connection.realClose();
        }
    }
}
