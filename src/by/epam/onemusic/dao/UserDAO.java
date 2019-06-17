/**
 * Created by Alexander Lomat on 13.05.19
 * version 0.0.4
 */

package by.epam.onemusic.dao;

import by.epam.onemusic.entity.User;
import by.epam.onemusic.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//TODO LOGS

public class UserDAO extends AbstractDAO<String, User> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Language("SQL")
    private static final String SELECT_ALL_USERS = "SELECT id, username, password, name," +
            " surname, is_admin, balance FROM user";
    @Language("SQL")
    private static final String SELECT_USER_BY_USERNAME = "SELECT id, username, password, name," +
            " surname, is_admin, balance FROM user WHERE username = ?";
    @Language("SQL")
    private static final String DELETE_USER_BY_USERNAME = "DELETE FROM user WHERE user.username = ? AND user.is_admin != 1";
    @Language("SQL")
    private static final String UPDATE_USER_BY_USERNAME = "UPDATE user SET id = ?, username = ?, " +
            "password = ?, name = ?, surname = ?, is_admin = ?, " +
            "balance = ? WHERE username = ? AND is_admin != 1";
    @Language("SQL")
    private static final String ADD_USER = "INSERT INTO user values (?,?,?,?,?,?,?)";

    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public UserDAO(ProxyConnection connection) {
        super(connection);
    }

    @Override
    public List findAll() {
        List<User> users = new ArrayList<>();
        preparedStatement = getPrepareStatement(SELECT_ALL_USERS);
        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                initializeUser(user, resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all users. ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return users;
    }

    @Override
    public User findByValue(String username) {
        User user = new User();
        preparedStatement = getPrepareStatement(SELECT_USER_BY_USERNAME);
        try {
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeUser(user, resultSet);
                return user;
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting user with username= " + username + " ", e);
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean deleteByKey(String username) {
        preparedStatement = getPrepareStatement(DELETE_USER_BY_USERNAME);
        try {
            preparedStatement.setString(1, username);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting user with username= " + username + " ", e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addNew(User entity) {
        preparedStatement = getPrepareStatement(ADD_USER);
        try {
            setUserPreparedStatement(entity, preparedStatement);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while adding new user ", e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(User entity, String username) {
        preparedStatement = getPrepareStatement(UPDATE_USER_BY_USERNAME);
        try {
            setUserPreparedStatement(entity, preparedStatement);
            preparedStatement.setString(8, username);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while updating user " + entity + " with username = " + username + " ", e);
            e.printStackTrace();
        }
        return false;
    }

    private void initializeUser(User user, ResultSet resultSet) throws SQLException {
        user.setId(resultSet.getInt(1));
        user.setLogin(resultSet.getString(2));
        user.setPassword(resultSet.getString(3));
        user.setName(resultSet.getString(4));
        user.setSurname(resultSet.getString(5));
        user.setAdmin(resultSet.getBoolean(6));
        user.setBalance(resultSet.getBigDecimal(7));
    }

    private void setUserPreparedStatement(User entity, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Long.toString(entity.getId()));
        preparedStatement.setString(2, entity.getLogin());
        preparedStatement.setString(3, entity.getPassword());
        preparedStatement.setString(4, entity.getName());
        preparedStatement.setString(5, entity.getSurname());
        preparedStatement.setString(6, Integer.toString(entity.isAdmin()));
        preparedStatement.setString(7, (entity.getBalance().toString()));
    }

}
