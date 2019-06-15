package by.epam.onemusic.dao;

import by.epam.onemusic.entity.User;
import by.epam.onemusic.pool.ProxyConnection;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//TODO LOGS
//TODO Playlists + Songs ??? ??? ???

public class UserDAO extends AbstractDAO<Integer, User> {

    @Language("SQL")
    private static final String SELECT_ALL_USERS = "SELECT id, username, password, name," +
            " surname, is_admin, balance FROM user";
    @Language("SQL")
    private static final String SELECT_USER_BY_ID = "SELECT id, username, password, name," +
            " surname, is_admin, balance FROM user WHERE id = ?";
    @Language("SQL")
    private static final String DELETE_USER_BY_ID = "DELETE FROM user WHERE user.id = ? AND user.is_admin != 1";
    @Language("SQL")
    private static final String UUPDATE_USER_BY_ID = "UPDATE user SET id = ?, username = ?, " +
            "password = ?, name = ?, surname = ?, is_admin = ?, " +
            "balance = ? WHERE id = ? AND is_admin != 1";
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
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return users;
    }

    @Override
    public User findEntityById(Integer id) {
        User user = new User();
        preparedStatement = getPrepareStatement(SELECT_USER_BY_ID);
        try {
            preparedStatement.setString(1, Integer.toString(id));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeUser(user, resultSet);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean deleteByKey(Integer id) {
        preparedStatement = getPrepareStatement(DELETE_USER_BY_ID);
        try {
            preparedStatement.setString(1, Integer.toString(id));
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean create(User entity) {
        preparedStatement = getPrepareStatement(ADD_USER);
        try {
            setPreparedStatement(entity, preparedStatement);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(User entity, Integer id) {
        preparedStatement = getPrepareStatement(UUPDATE_USER_BY_ID);
        try {
            setPreparedStatement(entity, preparedStatement);
            preparedStatement.setString(8, Integer.toString(id));
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
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

    private void setPreparedStatement(User entity, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Integer.toString(entity.getId()));
        preparedStatement.setString(2, entity.getLogin());
        preparedStatement.setString(3, entity.getPassword());
        preparedStatement.setString(4, entity.getName());
        preparedStatement.setString(5, entity.getSurname());
        preparedStatement.setString(6, Integer.toString(entity.isAdmin()));
        preparedStatement.setString(7, (entity.getBalance().toString()));
    }

    private boolean executePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
        int result = preparedStatement.executeUpdate();
        return result == 1;
    }

}
