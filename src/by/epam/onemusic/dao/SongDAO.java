/**
 * Created by Alexander Lomat on 13.05.19
 * version 0.0.4
 */

package by.epam.onemusic.dao;

import by.epam.onemusic.entity.Author;
import by.epam.onemusic.entity.Playlist;
import by.epam.onemusic.entity.Song;
import by.epam.onemusic.pool.ConnectionPool;
import by.epam.onemusic.pool.ProxyConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDAO extends AbstractDAO<String, Song> {

    private static final Logger LOGGER = LogManager.getLogger();

    //maybe to superclass?
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Language("SQL")
    private static final String SELECT_ALL_SONGS = "SELECT song.id, song.name, song.length, song.cost," +
            " song.establishmentYear, song.genre, a.id, a.name, a.country " +
            "FROM song INNER JOIN author a on song.author_id = a.id";

    @Language("SQL")
    private static final String SELECT_ALL_AUTHORS = "SELECT id, name, country FROM author";

    @Language("SQL")
    private static final String SELECT_SONG_BY_NAME = "SELECT song.id, song.name, song.length, song.cost," +
            " song.establishmentYear, song.genre, a.id, a.name, a.country " +
            "FROM song INNER JOIN author a on song.author_id = a.id WHERE song.name = ?";

    @Language("SQL")
    private static final String SELECT_AUTHOR_BY_NAME = "SELECT id, name, country FROM author WHERE author.name = ?";

    @Language("SQL")
    private static final String SELECT_AUTHOR_BY_SONG_NAME = "SELECT author.id, author.name, author.country FROM author " +
            "LEFT JOIN song s on author.id = s.author_id WHERE s.name = ?";

    @Language("SQL")
    private static final String SELECT_ALL_AUTHOR_SONGS_BY_AUTHOR_NAME = "SELECT song.id, song.name, song.length, song.cost," +
            " song.establishmentYear, song.genre " +
            "FROM song INNER JOIN author a on song.author_id = a.id WHERE a.name = ?";

    @Language("SQL")
    private static final String SELECT_ALL_USER_SONGS = "SELECT * FROM song INNER JOIN author a on song.author_id = a.id " +
            "RIGHT JOIN playlist_song ps on song.id = ps.song_id " +
            "RIGHT JOIN playlist p on ps.playlist_id = p.id RIGHT JOIN user_playlist up on p.id = up.playlist_id " +
            "RIGHT JOIN user u on up.user_id = u.id WHERE u.username = ?";

    @Language("SQL")
    private static final String[] ADD_SONG_WITH_AUTHOR = {"INSERT INTO author VALUES (?,?,?)",
            "INSERT INTO song VALUES (?,?,?,?,?,?,?)"};

    @Language("SQL")
    private static final String[] ADD_SONG_WITH_AUTHOR_NO_ID = {"INSERT INTO author VALUES (?,?)",
            "INSERT INTO song VALUES (?,?,?,?,?,?)"};

    @Language("SQL")
    private static final String ADD_SONG_TO_AUTHOR = "INSERT INTO song VALUES (?,?,?,?,?,?,?)";

    @Language("SQL")
    private static final String DELETE_SONG_BY_NAME = "DELETE FROM song WHERE song.name = ?";

    @Language("SQL")
    private static final String DELETE_ALL_AUTHOR_SONGS = "DELETE FROM song WHERE song.name = ?";

    @Language("SQL")
    private static final String DELETE_AUTHOR_WITH_SONGS_BY_NAME = "DELETE FROM author WHERE author.name = ?";

    @Language("SQL")
    private static final String[] UPDATE_SONG_BY_AUTHOR_NAME = {"UPDATE author SET id = ?, name = ?, country = ? " +
            "WHERE name = ?",
            "UPDATE song SET id = ?, name = ?, length = ?, cost = ?, establishmentYear = ?, genre = ?, " +
                    "author_id = ?"};

    @Language("SQL")
    private static final String UPDATE_SONG_BY_NAME = "UPDATE song SET name = ?, length = ?, cost = ?," +
            " establishmentYear = ?, genre = ?, author_id = ? WHERE name = ?";

    @Language("SQL")
    public static final String SELECT_ALL_SONGS_FROM_PLAYLIST = "SELECT * FROM song LEFT JOIN playlist_song " +
            "ps on song.id = ps.song_id" + " LEFT JOIN playlist p on ps.playlist_id = p.id WHERE p.name = ? ";

    @Language("SQL")
    public static final String ADD_SONG_TO_PLAYLIST = "INSERT INTO playlist_song values (?, ?)";


    public SongDAO(ProxyConnection connection) {
        super(connection);
    }

    @Override
    public List<Song> findAll() {
        List<Song> songs = new ArrayList<>();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_SONGS);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                initializeSongWithAuthor(song, resultSet);
                songs.add(song);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all songs. ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return songs;
    }

    public List<Song> findAllSongsOfUser (String username) {
        List<Song> songs = new ArrayList<>();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_USER_SONGS);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                initializeSongWithAuthor(song, resultSet);
                songs.add(song);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all songs. ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return songs;
    }

    private Author findAuthor(String authorName, String byWhat) {
        Author author = new Author();
        preparedStatement = getPrepareStatement(byWhat);
        try {
            preparedStatement.setString(1, authorName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeAuthor(author, resultSet);
                return author;
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting author with name = " + authorName + " ", e);
            e.printStackTrace();
        }
        return author;
    }

    public Author findAuthorBySongName(String name) {
        return findAuthor(name, SELECT_AUTHOR_BY_SONG_NAME);
    }

    public Author fingAuthorByAuthorName(String name) {
        return findAuthor(name, SELECT_AUTHOR_BY_NAME);
    }

    public List<Song> findAuthorSongsByAuthorName(String authorName) {
        List<Song> songs = new ArrayList<>();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_AUTHOR_SONGS_BY_AUTHOR_NAME);
            preparedStatement.setString(1, authorName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                initializeSongWithoutAuthor(song, resultSet);
                songs.add(song);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting author songs by author name = " + authorName + " ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return songs;
    }

    public List<Author> findAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_AUTHORS);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Author author = new Author();
                initializeAuthor(author, resultSet);
                authors.add(author);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all authors. ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return authors;
    }

    @Override
    public Song findByValue(String songName) {
        Song song = new Song();
        preparedStatement = getPrepareStatement(SELECT_SONG_BY_NAME);
        try {
            preparedStatement.setString(1, songName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeSongWithAuthor(song, resultSet);
                return song;
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting song with name = " + songName + " ", e);
            e.printStackTrace();
        }
        return song;
    }

    @Override
    public boolean deleteByKey(String songName) {
        try {
            preparedStatement = getPrepareStatement(DELETE_SONG_BY_NAME);
            preparedStatement.setString(1, songName);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting song with name = " + songName + " ", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAuthorWithHisSongs(String authorName) {
        preparedStatement = getPrepareStatement(DELETE_AUTHOR_WITH_SONGS_BY_NAME);
        try {
            preparedStatement.setString(1, authorName);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting author + songs with author name = " + authorName + " ", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllAuthorSongs(String authorName) {
        preparedStatement = getPrepareStatement(DELETE_ALL_AUTHOR_SONGS);
        try {
            preparedStatement.setString(1, authorName);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting all author songs. ", e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addNew(Song song) {
        Author author = song.getAuthor();
        try {
            setConnectionAutocommit(false);
            executeAuthorTransactionPart(author, ADD_SONG_WITH_AUTHOR[0], true);
            executeSongTransactionPart(song, ADD_SONG_WITH_AUTHOR[1], true);
            commitConnectionTransaction();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while adding new song. ", e);
            rollbackConnectionTransaction();
            e.printStackTrace();
        } finally {
            setConnectionAutocommit(true);
        }
        return false;
    }

//    public boolean addNewNoID(Song song) {
//        Author author = song.getAuthor();
//        try {
//            setConnectionAutocommit(false);
//            executeAuthorTransactionPartNoID(author, ADD_SONG_WITH_AUTHOR_NO_ID[0], true);
//            executeSongTransactionPartNoID(song, ADD_SONG_WITH_AUTHOR_NO_ID[1], true);
//            commitConnectionTransaction();
//            return true;
//        } catch (SQLException e) {
//            LOGGER.error("Error while adding new song no ID. ", e);
//            rollbackConnectionTransaction();
//            e.printStackTrace();
//        } finally {
//            setConnectionAutocommit(true);
//        }
//        return false;
//    }

    @Override
    public boolean update(Song song, String name) {
        Author author = new Author();
        try {
            setConnectionAutocommit(false);
            executeAuthorTransactionPart(author, UPDATE_SONG_BY_AUTHOR_NAME[0], false);
            preparedStatement.setString(4, name);
            executePreparedStatement(preparedStatement);
            executeSongTransactionPart(song, UPDATE_SONG_BY_AUTHOR_NAME[1], true);
            commitConnectionTransaction();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while updating song " + song + " with name = " + name + " ", e);
            rollbackConnectionTransaction();
            e.printStackTrace();
        } finally {
            setConnectionAutocommit(true);
        }
        return false;
    }

    public boolean updateSongInfoByName(Song song, String name) {
        try {
            preparedStatement = getPrepareStatement(UPDATE_SONG_BY_NAME);
            preparedStatement.setString(1, song.getSongName());
            preparedStatement.setString(2, Long.toString(song.getLength()));
            preparedStatement.setString(3, song.getCost().toString());
            preparedStatement.setString(4, Integer.toString(song.getEstablishmentYear()));
            preparedStatement.setString(5, song.getGenre());
            preparedStatement.setString(6, Long.toString(song.getAuthor().getId()));
            preparedStatement.setString(7, name);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while updating song " + song + " with name = " + name + " ", e);
            rollbackConnectionTransaction();
            e.printStackTrace();
        }
        return false;
    }

    public boolean addSongToAuthor(Song song) {
        try {
            preparedStatement = getPrepareStatement(ADD_SONG_TO_AUTHOR);
            setSongPreparedStatement(song, preparedStatement);
            executePreparedStatement(preparedStatement);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while adding song to author. ", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean addSongToPlaylist(String playlistName, String songName) {
        try {
            ProxyConnection playlistConnection = ConnectionPool.getInstance().takeConnection();
            ProxyConnection songConnection = ConnectionPool.getInstance().takeConnection();
            PlaylistDAO playlistDAO = new PlaylistDAO(playlistConnection);
            SongDAO songDAO = new SongDAO(songConnection);
            long playlistId = playlistDAO.findByValue(playlistName).getId();
            long songId =  songDAO.findByValue(songName).getId();
            preparedStatement = getPrepareStatement(ADD_SONG_TO_PLAYLIST);
            preparedStatement.setString(1, Long.toString(playlistId));
            preparedStatement.setString(2, Long.toString(songId));
            executePreparedStatement(preparedStatement);
            playlistDAO.closeConnection(playlistConnection);
            songDAO.closeConnection(songConnection);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while adding song to playlist. ", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean addSongToPlaylist(Song song, Playlist playlist) {
        try {
            preparedStatement = getPrepareStatement(ADD_SONG_TO_PLAYLIST);
            preparedStatement.setString(1, Long.toString(song.getId()));
            preparedStatement.setString(2, Long.toString(playlist.getId()));
            executePreparedStatement(preparedStatement);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while adding song to playlist. ", e);
            e.printStackTrace();
        }
        return false;
    }


    private void executeAuthorTransactionPart(Author author, String transactionQuery, boolean execute) throws SQLException {
        preparedStatement = getPrepareStatement(transactionQuery);
        setAuthorPreparedStatement(author, preparedStatement);
        if (execute) {
            executePreparedStatement(preparedStatement);
        }
    }

    private void executeSongTransactionPart(Song song, String transactionQuery, boolean execute) throws SQLException {
        preparedStatement = getPrepareStatement(transactionQuery);
        setSongPreparedStatement(song, preparedStatement);
        if (execute) {
            executePreparedStatement(preparedStatement);
        }
    }

    private void initializeSongWithAuthor(Song song, ResultSet resultSet) throws SQLException {
        initializeSongWithoutAuthor(song, resultSet);
        Author author = new Author(resultSet.getInt(7), resultSet.getString(8),
                resultSet.getString(9));
        song.setAuthor(author);
    }

    private void initializeSongWithoutAuthor(Song song, ResultSet resultSet) throws SQLException {
        song.setId(resultSet.getInt(1));
        song.setSongName(resultSet.getString(2));
        song.setLength(resultSet.getLong(3));
        song.setCost(resultSet.getBigDecimal(4));
        song.setEstablishmentYear(resultSet.getInt(5));
        song.setGenre(resultSet.getString(6));
    }

    private void initializeAuthor(Author author, ResultSet resultSet) throws SQLException {
        author.setId(resultSet.getInt(1));
        author.setName(resultSet.getString(2));
        author.setCountry(resultSet.getString(3));
    }

    private void setAuthorPreparedStatement(Author author, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Long.toString(author.getId()));
        preparedStatement.setString(2, author.getName());
        preparedStatement.setString(3, author.getCountry());
    }

    private void setSongPreparedStatement(Song song, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Long.toString(song.getId()));
        preparedStatement.setString(2, song.getSongName());
        preparedStatement.setString(3, Long.toString(song.getLength()));
        preparedStatement.setString(4, song.getCost().toString());
        preparedStatement.setString(5, Integer.toString(song.getEstablishmentYear()));
        preparedStatement.setString(6, song.getGenre());
        preparedStatement.setString(7, Long.toString(song.getAuthor().getId()));
    }

//    private void setAuthorPreparedStatementNoID(Author author, PreparedStatement preparedStatement) throws SQLException {
//        preparedStatement.setString(1, author.getName());
//        preparedStatement.setString(2, author.getCountry());
//    }
//
//    private void setSongPreparedStatementNoID(Song song, PreparedStatement preparedStatement) throws SQLException {
//        preparedStatement.setString(1, song.getSongName());
//        preparedStatement.setString(2, Long.toString(song.getLength()));
//        preparedStatement.setString(3, song.getCost().toString());
//        preparedStatement.setString(4, Integer.toString(song.getEstablishmentYear()));
//        preparedStatement.setString(5, song.getGenre());
//        preparedStatement.setString(6, Long.toString(song.getAuthor().getId()));
//    }
//
//    private void executeAuthorTransactionPartNoID(Author author, String transactionQuery, boolean execute) throws SQLException {
//        preparedStatement = getPrepareStatement(transactionQuery);
//        setAuthorPreparedStatementNoID(author, preparedStatement);
//        if (execute) {
//            executePreparedStatement(preparedStatement);
//        }
//    }
//
//    private void executeSongTransactionPartNoID(Song song, String transactionQuery, boolean execute) throws SQLException {
//        preparedStatement = getPrepareStatement(transactionQuery);
//        setSongPreparedStatementNoID(song, preparedStatement);
//        if (execute) {
//            executePreparedStatement(preparedStatement);
//        }
//    }

}
