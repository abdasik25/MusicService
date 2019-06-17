/**
 * Created by Alexander Lomat on 15.05.19
 * version 0.0.1
 */

package by.epam.onemusic.util;

import by.epam.onemusic.dao.PlaylistDAO;
import by.epam.onemusic.dao.SongDAO;
import by.epam.onemusic.entity.Playlist;
import by.epam.onemusic.entity.Song;
import by.epam.onemusic.pool.ConnectionPool;
import by.epam.onemusic.pool.ProxyConnection;

import java.util.List;

public class IdGenerator {

    private static IdGenerator instance;
    private static long idAuthorCounter = 1;
    private static long idSongCounter = 1;
    private static long idPlaylistCounter = 1;

    private IdGenerator() {
        ProxyConnection connection = ConnectionPool.getInstance().takeConnection();
        SongDAO songDAO = new SongDAO(connection);
        PlaylistDAO playlistDAO = new PlaylistDAO(connection);
        List<Song> songList = songDAO.findAll();
        List<Playlist> playlistList = playlistDAO.findAll();
        if (!songList.isEmpty() && songList.size() != 1) {
            idSongCounter = songList.get(songList.size() - 1).getId();
            idAuthorCounter = songList.get(songList.size() - 1).getAuthor().getId();
        }
        if (!playlistList.isEmpty() && playlistList.size() != 1) {
            idPlaylistCounter = playlistList.get(playlistList.size() - 1).getId();
        }
        songDAO.closeConnection(connection);
    }

    public static IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    public long generateIdAuthorID() {
        return idAuthorCounter++;
    }

    public long generateSongId() {
        return idSongCounter++;
    }

    public long generatePlaylistId(){
        return idPlaylistCounter++;
    }

}
