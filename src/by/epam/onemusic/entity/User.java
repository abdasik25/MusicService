/**
 * Created by Alexander Lomat on 13.05.19
 * version 0.0.1
 */

package by.epam.onemusic.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class User extends Entity {

    private String login;
    private String password;
    private String name;
    private String surname;
    private boolean isAdmin;
    private BigDecimal balance;

    private List<Playlist> playlists;
    private List<Song> songs;

    public User() {
    }

    public User(long id, String login, String password, String name, String surname,
                boolean isAdmin, BigDecimal balance, List<Playlist> playlists, List<Song> songs) {
        super(id);
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.isAdmin = isAdmin;
        this.balance = balance;
        this.playlists = playlists;
        this.songs = songs;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int isAdmin() {
        return isAdmin ? 1 : 0;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return isAdmin == user.isAdmin &&
                login.equals(user.login) &&
                password.equals(user.password) &&
                name.equals(user.name) &&
                surname.equals(user.surname) &&
                Objects.equals(balance, user.balance) &&
                playlists.equals(user.playlists) &&
                songs.equals(user.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password, name, surname, isAdmin, balance, playlists, songs);
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", isAdmin=" + isAdmin +
                ", balance=" + balance +
                ", playlists=" + playlists +
                ", songs=" + songs +
                '}';
    }
}
