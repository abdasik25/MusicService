import by.epam.onemusic.dao.AdminDAO;
import by.epam.onemusic.entity.User;
import by.epam.onemusic.pool.ConnectionPool;
import by.epam.onemusic.pool.ProxyConnection;

import java.math.BigDecimal;
import java.util.List;

public class Main {


    public static void main(String[] args) {

//        String SQL_INSERT = "INSERT INTO user(id, username, password, name, surname) VALUES(?,?,?,?,?)";
//        String SQL_SELECT = "SELECT * FROM user";
//        try {
//            PreparedStatement preparedStatement = ConnectionPool.getInstance().takeConnection().prepareStatement(SQL_SELECT);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            User user = new User();
//            while (resultSet.next()) {
//                user.setLogin(resultSet.getString(2));
//                user.setId(resultSet.getInt(1));
//                user.setPassword(resultSet.getString(3));
//                user.setName(resultSet.getString(4));
//                user.setSurname(resultSet.getString(5));
//                user.setAdmin(resultSet.getBoolean(6));
//                user.setBalance(resultSet.getBigDecimal(7));
//            }
//            preparedStatement.setString(1, "2");
//            preparedStatement.setString(2, "abdasik25");
//            preparedStatement.setString(3, "5400054000");
//            preparedStatement.setString(4, "alexander");
//            preparedStatement.setString(5, "lomat");
//            preparedStatement.executeUpdate();
//            preparedStatement.close();
//            resultSet.close();
//            System.out.println(user);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//        }
        User user = new User(3, "alina.akrami", "alina111", "alina", "akrami", false, new BigDecimal(20), null, null);
        ProxyConnection сonnection = ConnectionPool.getInstance().takeConnection();
        AdminDAO adminDAO = new AdminDAO(сonnection);
        List<User> userList = adminDAO.findAll();
        for (int i = 0; i < userList.size(); i++) {
            System.out.println(userList.get(i));
        }
        System.out.println(adminDAO.create(user));
        System.out.println(adminDAO.findEntityById(1));
        System.out.println(adminDAO.deleteByKey(1));
        adminDAO.closeConnection(сonnection);
    }
}
