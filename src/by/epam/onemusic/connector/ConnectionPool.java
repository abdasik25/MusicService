package by.epam.onemusic.connector;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionPool {
    private static ConnectionPool instance;
    //TODO PROXY-CONNECTIONS
    private BlockingDeque<ProxyConnection> freeConnections;
    private static ReentrantLock reentrantLock = new ReentrantLock();
    private static AtomicBoolean isCreated = new AtomicBoolean(false);

    private ConnectionPool() {

    }

    public static ConnectionPool getInstance() {
        if(!isCreated.get()) {
            try {
                reentrantLock.lock();
                if (instance == null) {
                    instance = new ConnectionPool();
                    isCreated.set(true);
                }
            } finally {
                reentrantLock.unlock();
            }
            return instance;
        }
    }


    Connection connection;

    public static ConnectionPool getConnectionPool() {
        return new ConnectionPool();
    }

    public Connection getConnection() {
        return connection;
    }

    public void returnConnection(Connection connection) {
    }
}
