package by.epam.onemusic.entity;

import java.util.List;
import java.util.Objects;

public class Playlist extends Entity {

    private String name;
    private List<Song> playlist;

    public Playlist(){

    }
    public Playlist(int id, String name, List<Song> playlist) {
        super(id);
        this.name = name;
        this.playlist = playlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        Playlist playlist1 = (Playlist) o;
        return name.equals(playlist1.name) &&
                playlist.equals(playlist1.playlist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, playlist);
    }
}
