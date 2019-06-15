import by.epam.onemusic.dao.SongDAO;
import by.epam.onemusic.dao.UserDAO;
import by.epam.onemusic.entity.Song;
import by.epam.onemusic.entity.User;
import by.epam.onemusic.pool.ConnectionPool;
import by.epam.onemusic.pool.ProxyConnection;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        User alina = new User(3, "alina.akrami", "alina111", "alina", "akrami", false,
                new BigDecimal(20), null, null);
        User anzhelika = new User(4, "a.zavistovskaya", "zhika-zhizhika", "anzhelika", "zavistovskaya",
                false, new BigDecimal(30), null, null);

        ProxyConnection сonnection1 = ConnectionPool.getInstance().takeConnection();
        ProxyConnection сonnection2 = ConnectionPool.getInstance().takeConnection();
        UserDAO userDAO = new UserDAO(сonnection1);
        List<User> userList = userDAO.findAll();
        for (int i = 0; i < userList.size(); i++) {
            System.out.println(userList.get(i));
        }
        SongDAO songDAO = new SongDAO(сonnection2);
        List<Song> songs = songDAO.findAll();
        for (int i = 0; i < songs.size(); i++) {
            System.out.println(songs.get(i));
        }
        System.out.println(userDAO.create(alina));
        System.out.println(userDAO.update(anzhelika, 3));
        System.out.println(userDAO.findEntityById(1));
        System.out.println(userDAO.deleteByKey(1));
        System.out.println("-----------------------");
        System.out.println(songDAO.findAuthorBySongId(1));
        System.out.println("-----------------------");
        List<Song> songs1 = songDAO.findAuthorSongsByAuthorId(1);
        for (int i = 0; i < songs.size(); i++) {
            System.out.println(songs1.get(i));
        }
        userDAO.closeConnection(сonnection1);
        userDAO.closeConnection(сonnection2);
        try {
            ConnectionPool.getInstance().closePool();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
