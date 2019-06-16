package by.epam.onemusic.entity;

import java.io.Serializable;

public class Entity implements Serializable, Cloneable {

    private long id;

    public Entity() {
    }

    public Entity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
