package by.epam.onemusic.dao;

import by.epam.onemusic.entity.Entity;
import by.epam.onemusic.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public abstract class AbstractDAO<Key, T extends Entity> {

    private static final Logger LOGGER = LogManager.getLogger();

    private ProxyConnection connection;

    public AbstractDAO() {
    }

    public AbstractDAO(ProxyConnection connection) {
        this.connection = connection;
    }

    public abstract List<T> findAll();

    public abstract T findEntityById(Key id);

    public abstract boolean deleteByKey(Key id);

    public abstract boolean deleteByEntity(T entity);

    public abstract boolean create(T entity);

    public abstract T update(T entity); //добавляется измененный а возвращается который изменили


    // Получение экземпляра PrepareStatement
    public PreparedStatement getPrepareStatement(String sql) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ps;
    }

    // Закрытие PrepareStatement
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

    public  void closeConnection(ProxyConnection connection) {
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

}
