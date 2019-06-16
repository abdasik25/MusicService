package by.epam.onemusic.entity;

import java.util.Objects;

public class Author extends Entity {

    private String name;
    private String country;

    public Author(){
    }

    public Author(long id, String name, String country) {
        super(id);
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author author = (Author) o;
        return name.equals(author.name) &&
                Objects.equals(country, author.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country);
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
