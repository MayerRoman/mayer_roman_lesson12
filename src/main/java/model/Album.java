package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayer Roman on 29.05.2016.
 */

@XmlType(propOrder = {"id", "name", "genre", "songs"})
public class Album {
    private int id;

    private String name;

    private String genre;

    private List<Song> songs = new ArrayList<>();


    public Album() {}

    public Album(int id, String name, String genre) {
        this.id = id;
        this.name = name;
        this.genre = genre;
    }


    public int getId() {
        return id;
    }

    @XmlElement(name = "albumId")
    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    @XmlElement(name = "albumName")
    public void setName(String name) {
        this.name = name;
    }


    public String getGenre() {
        return genre;
    }

    @XmlElement
    public void setGenre(String genre) {
        this.genre = genre;
    }


    public List<Song> getSongs() {
        return songs;
    }

    public Song getSong(int songIndex) {
        return songs.get(songIndex);
    }


    @XmlElement(name = "song")
    @XmlElementWrapper
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setSong(Song song) {
        songs.add(song);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (id != album.id) return false;
        if (name != null ? !name.equals(album.name) : album.name != null) return false;
        if (genre != null ? !genre.equals(album.genre) : album.genre != null) return false;
        return songs != null ? songs.equals(album.songs) : album.songs == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + (songs != null ? songs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genre='" + genre + '\'' +
                ", songs=" + songs +
                '}';
    }
}
