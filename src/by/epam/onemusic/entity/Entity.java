/**
 * Created by Alexander Lomat on 13.05.19
 * version 0.0.1
 */

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
