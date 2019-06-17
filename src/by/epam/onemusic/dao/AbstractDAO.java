/**
 * Created by Alexander Lomat on 13.05.19
 * version 0.0.2
 */

package by.epam.onemusic.dao;

import by.epam.onemusic.entity.Entity;
import by.epam.onemusic.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public abstract class AbstractDAO<K, T extends Entity> {

    private static final Logger LOGGER = LogManager.getLogger();

    private ProxyConnection connection;


    public AbstractDAO(ProxyConnection connection) {
        this.connection = connection;
    }

    public abstract List<T> findAll();

    public abstract T findByValue(K value);

    public abstract boolean deleteByKey(K key);

    public abstract boolean addNew(T entity);

    public abstract boolean update(T entity, K key);

    public PreparedStatement getPrepareStatement(String sql) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            LOGGER.error("Can't get prepared statement.");
            e.printStackTrace();
        }

        return ps;
    }

    public static void closePrepareStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                LOGGER.error("Can't close prepared statement.");
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
                LOGGER.info("ResultSet was closed.");
            } catch (SQLException e) {
                LOGGER.error("Can't close result set.");
            }
        }
    }

    public void closeConnection(ProxyConnection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Connection was returned to pool.");
            } catch (SQLException e) {
                LOGGER.error("Can't close connection");
                e.printStackTrace();
            }
        }

    }

    protected boolean setConnectionAutocommit(boolean value) {
        try {
            connection.setAutoCommit(value);
            LOGGER.info("Transaction autocommit status was set to " + value);
            return true;
        } catch (SQLException e) {
            LOGGER.info("Error while setting autocommit. ");
            e.printStackTrace();
        }
        return false;
    }

    protected boolean commitConnectionTransaction() {
        try {
            connection.commit();
            LOGGER.info("Transaction was commited");
            return true;
        } catch (SQLException e) {
            LOGGER.info("Error while commiting transaction. ");
            e.printStackTrace();
        }
        return false;
    }

    protected boolean rollbackConnectionTransaction() {
        try {
            connection.commit();
            LOGGER.info("Transaction was rollbacked. ");
            return true;
        } catch (SQLException e) {
            LOGGER.info("Error while rollbacking transaction.");
            e.printStackTrace();
        }
        return false;
    }

    protected boolean executePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
        int result = preparedStatement.executeUpdate();
        return result == 1;
    }


}
