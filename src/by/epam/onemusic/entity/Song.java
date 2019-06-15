package by.epam.onemusic.entity;

import java.math.BigDecimal;
import java.util.Objects;

public class Song extends Entity {

    private String songName;
    private long length;
    private BigDecimal cost;
    private int establishmentYear;
    private String genre;
    private Author author;

    public Song() {
    }

    public Song(int id, String songName, long length, BigDecimal cost, int establishmentYear, String genre, Author author) {
        super(id);
        this.songName = songName;
        this.length = length;
        this.cost = cost;
        this.establishmentYear = establishmentYear;
        this.genre = genre;
        this.author = author;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public int getEstablishmentYear() {
        return establishmentYear;
    }

    public void setEstablishmentYear(int establishmentYear) {
        this.establishmentYear = establishmentYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    //TODO Override Methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return length == song.length &&
                establishmentYear == song.establishmentYear &&
                songName.equals(song.songName) &&
                cost.equals(song.cost) &&
                genre.equals(song.genre) &&
                author.equals(song.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songName, length, cost, establishmentYear, genre, author);
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", length=" + length +
                ", cost=" + cost +
                ", establishmentYear=" + establishmentYear +
                ", genre='" + genre + '\'' +
                ", author=" + author +
                '}';
    }
}
