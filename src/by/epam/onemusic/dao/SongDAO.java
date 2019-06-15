package by.epam.onemusic.dao;

import by.epam.onemusic.entity.Author;
import by.epam.onemusic.entity.Song;
import by.epam.onemusic.pool.ProxyConnection;
import org.intellij.lang.annotations.Language;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDAO extends AbstractDAO<Integer, Song> {

    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Language("SQL")
    private static final String SELECT_ALL_SONGS = "SELECT song.id, song.name, song.length, song.cost," +
            " song.establishmentYear, song.genre, a.id, a.name, a.country " +
            "FROM song INNER JOIN author a on song.author_id = a.id";
    @Language("SQL")
    private static final String SELECT_ALL_AUTHORS = "SELECT id, name, country FROM author";
    @Language("SQL")
    private static final String SELECT_SONG_BY_ID = "SELECT song.id, song.name, song.length, song.cost," +
            " song.establishmentYear, song.genre, a.id, a.name, a.country " +
            "FROM song INNER JOIN author a on song.author_id = a.id WHERE song.id = ?";
    @Language("SQL")
    private static final String SELECT_AUTHOR_BY_ID = "SELECT id, name, country FROM author WHERE author.id = ?";
    @Language("SQL")
    private static final String SELECT_AUTHOR_BY_SONG_ID = "SELECT author.id, author.name, author.country FROM author " +
            "LEFT JOIN song s on author.id = s.author_id WHERE s.id = ?";
    @Language("SQL")
    private static final String SELECT_ALL_AUTHOR_SONGS_BY_AUTHOR_ID = "SELECT song.id, song.name, song.length, song.cost," +
            " song.establishmentYear, song.genre " +
            "FROM song INNER JOIN author a on song.author_id = a.id WHERE a.id = ?";
    @Language("SQL")
    private static final String ADD_SONG_WITH_AUTHOR[] = {"INSERT INTO author VALUES (?,?,?)",
            "INSERT INTO song VALUES (?,?,?,?,?,?,?)"};
    @Language("SQL")
    private static final String ADD_SONG_TO_AUTHOR = "INSERT INTO song VALUES (?,?,?,?,?,?,?)";
    @Language("SQL")
    private static final String DELETE_SONG_BY_ID = "DELETE FROM song WHERE song.id = ?";


//TODO ПО АЙДИ БУДЕТ СЛОЖНО ИСКАТЬ И НАХОДИТЬ ОБЫЧНОМУ ПОЛЬЗОВАТЕЛЮ, СДЕЛАТЬ ПО ЭНТИТИ???
//add song +
//delete song +
//delete all author songs
//delete author (with songs)
//update author info by id
//update song info by id


    public SongDAO() {
        super();
    }

    public SongDAO(ProxyConnection connection) {
        super(connection);
    }

    @Override
    public List<Song> findAll() {
        List<Song> songs = new ArrayList<>();
        preparedStatement = getPrepareStatement(SELECT_ALL_SONGS);
        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                initializeSongWithAuthor(song, resultSet);
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return songs;
    }

    private Author findAuthor(int id, String byWhat) {
        Author author = new Author();
        preparedStatement = getPrepareStatement(byWhat);
        try {
            preparedStatement.setString(1, Integer.toString(id));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeAuthor(author, resultSet);
                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return author;
    }

    public Author findAuthorBySongId(int id) {
        return findAuthor(id, SELECT_AUTHOR_BY_SONG_ID);
    }

    public Author fingAuthorById(int id) {
        return findAuthor(id, SELECT_AUTHOR_BY_ID);
    }

    public List<Song> findAuthorSongsByAuthorId(int id) {
        List<Song> songs = new ArrayList<>();
        preparedStatement = getPrepareStatement(SELECT_ALL_AUTHOR_SONGS_BY_AUTHOR_ID);
        try {
            preparedStatement.setString(1, Integer.toString(id));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                initializeSongWithoutAuthor(song, resultSet);
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return songs;
    }

    public List<Author> findAllAuthors() {
        List<Author> authors = new ArrayList<>();
        preparedStatement = getPrepareStatement(SELECT_ALL_AUTHORS);
        try {
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Author author = new Author();
                initializeAuthor(author, resultSet);
                authors.add(author);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closePrepareStatement(preparedStatement);
            closeResultSet(resultSet);
        }
        return authors;
    }


    @Override
    public Song findEntityById(Integer id) {
        Song song = new Song();
        preparedStatement = getPrepareStatement(SELECT_SONG_BY_ID);
        try {
            preparedStatement.setString(1, Integer.toString(id));
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                initializeSongWithAuthor(song, resultSet);
                return song;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }

    @Override
    public boolean deleteByKey(Integer id) {
        preparedStatement = getPrepareStatement(DELETE_SONG_BY_ID);
        try {
            preparedStatement.setString(1, Integer.toString(id));
            return executePreparedStatement(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean create(Song entity) {
        Author author = entity.getAuthor();
        try {
            super.setConnectionAutocommit(false);
            for (String QUERY : ADD_SONG_WITH_AUTHOR) {
                preparedStatement = getPrepareStatement(QUERY);
                setAuthorPreparedStatement(author, preparedStatement);
                executePreparedStatement(preparedStatement);
            }
            super.commitConnectionTransaction();
            return true;
        } catch (SQLException e) {
            // LOGGER.error("");
            super.rollbackConnectionTransaction();
            e.printStackTrace();
        } finally {
            super.setConnectionAutocommit(true);
        }
        return false;
    }

    public boolean addSongToAuthor(Song entity) {
        try {
            preparedStatement = getPrepareStatement(ADD_SONG_TO_AUTHOR);
            setSongPreparedStatement(entity, preparedStatement);
            executePreparedStatement(preparedStatement);
            return true;
        } catch (SQLException e) {
            // LOGGER.error("");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Song entity, Integer id) {
        return false;
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

    private void setAuthorPreparedStatement(Author entity, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Integer.toString(entity.getId()));
        preparedStatement.setString(2, entity.getName());
        preparedStatement.setString(3, entity.getCountry());
    }

    private void setSongPreparedStatement(Song entity, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, Integer.toString(entity.getId()));
        preparedStatement.setString(2, entity.getSongName());
        preparedStatement.setString(3, Long.toString(entity.getLength()));
        preparedStatement.setString(4, entity.getCost().toString());
        preparedStatement.setString(5, Integer.toString(entity.getEstablishmentYear()));
        preparedStatement.setString(6, entity.getGenre());
        preparedStatement.setString(7, Integer.toString(entity.getAuthor().getId()));
    }

    private boolean executePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
        int result = preparedStatement.executeUpdate();
        return result == 1;
    }


}
