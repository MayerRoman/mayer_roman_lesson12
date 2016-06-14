package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayer Roman on 29.05.2016.
 */
@XmlType(propOrder = {"id", "name", "albums"})
public class Singer {
    private int id;

    private String name;

    private List<Album> albums = new ArrayList<>();


    public Singer() {}

    public Singer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @XmlElement
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }


    public List<Album> getAlbums() {
        return albums;
    }

    public Album getAlbum(int albumIndex) {
        return albums.get(albumIndex);
    }


    @XmlElement(name = "album")
    @XmlElementWrapper
    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public void setAlbum(Album album) {
        albums.add(album);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Singer singer = (Singer) o;

        if (id != singer.id) return false;
        if (name != null ? !name.equals(singer.name) : singer.name != null) return false;
        return albums != null ? albums.equals(singer.albums) : singer.albums == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (albums != null ? albums.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Singer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", albums=" + albums +
                '}';
    }
}
