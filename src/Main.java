import by.epam.onemusic.dao.SongDAO;
import by.epam.onemusic.dao.UserDAO;
import by.epam.onemusic.entity.Author;
import by.epam.onemusic.entity.Song;
import by.epam.onemusic.entity.User;
import by.epam.onemusic.pool.ConnectionPool;
import by.epam.onemusic.pool.ProxyConnection;
import by.epam.onemusic.util.IdGenerator;
import by.epam.onemusic.util.PasswordEncryption;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        User alina = new User(3, "alina.akrami", PasswordEncryption.encryptPassword("alina111"), "alina", "akrami", false,
                new BigDecimal(20), null, null);
        User anzhelika = new User(4, "a.zavistovskaya", PasswordEncryption.encryptPassword("zhika-zhizhika"), "anzhelika", "zavistovskaya",
                false, new BigDecimal(30), null, null);
        //test

        ProxyConnection сonnection1 = ConnectionPool.getInstance().takeConnection();
        ProxyConnection сonnection2 = ConnectionPool.getInstance().takeConnection();
        ProxyConnection connection3 = ConnectionPool.getInstance().takeConnection();
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
        System.out.println(userDAO.addNew(alina));
        System.out.println(userDAO.addNew(anzhelika));
        System.out.println("-----------------------");
        System.out.println(songDAO.findAuthorBySongName("nothing else matters"));
        System.out.println("-----------------------");
        List<Song> songs1 = songDAO.findAuthorSongsByAuthorName("Metallica");
        for (int i = 0; i < songs.size(); i++) {
            System.out.println(songs1.get(i));
        }
        System.out.println("---------++++++-------");
        long idAuthor = IdGenerator.getInstance().generateIdAuthorID();
        Song song = new Song(IdGenerator.getInstance().generateSongId(),"nothing else matters", 200, new BigDecimal("4.99"),1991,"metal",
                new Author(idAuthor,"Metallica","USA"));
        songDAO.addSongToAuthor(song);
        Song song1 = new Song(IdGenerator.getInstance().generateSongId(),"Turn the page", 200, new BigDecimal("3.99"),1997,"metal",
                new Author(idAuthor,"Metallica","USA"));
        Song song2 = new Song("The unforgiven", 240, new BigDecimal("3.69"),1991,"metal",
                new Author(idAuthor,"Metallica","USA"));
        songDAO.addNew(song);
//        songDAO.addNewNoID(song2);
        songDAO.addSongToAuthor(song1);
        songDAO.addSongToPlaylist("Best Metal Songs", "Turn the page");

        System.out.println("---------//////////////-------");
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
