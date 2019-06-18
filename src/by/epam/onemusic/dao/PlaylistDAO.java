package by.epam.onemusic.dao;

import by.epam.onemusic.entity.Playlist;
import by.epam.onemusic.entity.User;
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

public class PlaylistDAO extends AbstractDAO<String, Playlist> {

    public PlaylistDAO(ProxyConnection connection) {
        super(connection);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    //TODO add song to playlist

    @Language("SQL")
    public static final String SELECT_ALL_PLAYLISTS = "SELECT id, name FROM playlist";

    @Language("SQL")
    public static final String SELECT_ALL_PLAYLISTS_WITH_SONG = "SELECT * FROM playlist RRIGHT JOIN " +
            "playlist_song ps on RRIGHT.id = ps.playlist_id RIGHT JOIN song s on ps.song_id = s.id WHERE s.name = ?";

    @Language("SQL")
    public static final String SELECT_PLAYLIST_BY_NAME = "SELECT id, name FROM playlist WHERE name = ?";

    @Language("SQL")
    public static final String SELECT_ALL_USER_PLAYLISTS = "SELECT * FROM playlist LEFT JOIN user_playlist up " +
            "on playlist.id = up.playlist_id LEFT JOIN user u on up.user_id = u.id WHERE u.username = ? ";

    @Language("SQL")
    public static final String DELETE_PLAYLIST_BY_NAME = "DELETE FROM playlist WHERE name = ?";

    @Language("SQL")
    public static final String UPDATE_PLAYLIST_BY_NAME = "UPDATE playlist SET name = ?";

    @Language("SQL")
    public static final String  ADD_NEW_PLAYLIST = "INSERT INTO playlist VALUES (?,?)";

    @Language("SQL")
    public static final String ADD_PLAYLIST_TO_USER = "INSERT INTO user_playlist values (?, ?)";

    public List<Playlist> findAllUserPlaylists(String username) {
        List<Playlist> playlists = new ArrayList<>();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_USER_PLAYLISTS);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Playlist playlist = new Playlist();
                initializeUserPlaylist(playlist, resultSet);
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all playlists of user " + username + " ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return playlists;
    }


    @Override
    public List<Playlist> findAll() {
        List<Playlist> playlists = new ArrayList<>();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_PLAYLISTS);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Playlist playlist = new Playlist();
                initializeUserPlaylist(playlist, resultSet);
                playlists.add(playlist);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all playlists. ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return playlists;
    }

    @Override
    public Playlist findByValue(String playlistName) {
        Playlist playlist = new Playlist();
        try {
            preparedStatement = getPrepareStatement(SELECT_PLAYLIST_BY_NAME);
            preparedStatement.setString(1, playlistName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeUserPlaylist(playlist, resultSet);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all playlists with name = " + playlistName + " ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return playlist;
    }

    public Playlist findPlaylistsWithSong(String songname) {
        Playlist playlist = new Playlist();
        try {
            preparedStatement = getPrepareStatement(SELECT_ALL_PLAYLISTS_WITH_SONG);
            preparedStatement.setString(1, songname);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeUserPlaylist(playlist, resultSet);
            }
        } catch (SQLException e) {
            LOGGER.error("Error while selecting all playlists with song name = " + songname + " ", e);
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return playlist;
    }

    @Override
    public boolean deleteByKey(String playlistName) {
        try {
            preparedStatement = getPrepareStatement(DELETE_PLAYLIST_BY_NAME);
            preparedStatement.setString(1, playlistName);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while deleting playlist with name = " + playlistName + " ", e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addNew(Playlist entity) {
        preparedStatement = getPrepareStatement(ADD_NEW_PLAYLIST);
        try {
            setPlaylistPreparedStatement(entity, preparedStatement);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while adding new playlist ", e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Playlist entity, String key) {
        preparedStatement = getPrepareStatement(UPDATE_PLAYLIST_BY_NAME);
        try {
            setPlaylistPreparedStatement(entity, preparedStatement);
            preparedStatement.setString(1, key);
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            LOGGER.error("Error while updating playlist " + entity + " with name = " + key + " ", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean addPlaylistToUser(String playlistName, String userName) {
        try {
            ProxyConnection userConnection = ConnectionPool.getInstance().takeConnection();
            UserDAO userDAO = new UserDAO(userConnection);
            long userId = userDAO.findByValue(userName).getId();
            long playlistId = findByValue(playlistName).getId();
            preparedStatement = getPrepareStatement(ADD_PLAYLIST_TO_USER);
            preparedStatement.setString(1, Long.toString(userId));
            preparedStatement.setString(2, Long.toString(playlistId));
            executePreparedStatement(preparedStatement);
            userDAO.closeConnection(userConnection);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while adding playlist name = " + playlistName + " to user name = " + userName +
                    " ", e);
            e.printStackTrace();
        }
        return false;
    }

    public boolean addPlaylistToUser(User user, Playlist playlist) {
        try {
            preparedStatement = getPrepareStatement(ADD_PLAYLIST_TO_USER);
            preparedStatement.setString(1, Long.toString(user.getId()));
            preparedStatement.setString(2, Long.toString(playlist.getId()));
            executePreparedStatement(preparedStatement);
            return true;
        } catch (SQLException e) {
            LOGGER.error("Error while adding user to playlist. ", e);
            e.printStackTrace();
        }
        return false;
    }


    private void initializeUserPlaylist(Playlist playlist, ResultSet resultSet) throws SQLException {
        playlist.setId(resultSet.getInt(1));
        playlist.setName(resultSet.getString(2));

    }

    private void setPlaylistPreparedStatement(Playlist entity, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Long.toString(entity.getId()));
        preparedStatement.setString(2, entity.getName());
    }
}
