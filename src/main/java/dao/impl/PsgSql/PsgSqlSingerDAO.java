package dao.impl.PsgSql;

import dao.SingerDAO;
import model.Album;
import model.Singer;
import model.SingersCatalog;
import model.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mayer Roman on 10.06.2016.
 */
class PsgSqlSingerDAO implements SingerDAO {
    private static final Logger LOGGER = LogManager.getLogger(PsgSqlSingerDAO.class);

    private Connection connection;

    PsgSqlSingerDAO(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void createSingersFromCatalog(SingersCatalog catalog) {

        String sql = "INSERT INTO singer VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Singer singer : catalog.getSingers()) {

                preparedStatement.setInt(1, singer.getId());
                preparedStatement.setString(2, singer.getName());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Inserting singers exception", e);
        }

        catalog.getSingers().stream()
                .filter(singer -> singer.getAlbums().size() > 0)
                .forEach(singer -> {
                    createAlbums(singer.getAlbums(), singer.getId());
                });

    }

    @Override
    public void createSinger(Singer singer) {
        String sql = "INSERT INTO singer VALUES (?, ?)";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, singer.getId());
            preparedStatement.setString(2, singer.getName());

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Inserting singer exception", e);
        }

        if (singer.getAlbums().size() > 0) {
            createAlbums(singer.getAlbums(), singer.getId());
        }
    }


    @Override
    public SingersCatalog readSingersCatalog() {
        SingersCatalog singersCatalog = new SingersCatalog();

        String sql = "SELECT * FROM singer";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            Singer singer;
            int singerId;

            while (resultSet.next()) {
                singer = new Singer();
                singerId = resultSet.getInt(1);

                singer.setId(singerId);
                singer.setName(resultSet.getString(2));
                singer.setAlbums(readAlbums(singerId));


                singersCatalog.addSinger(singer);
            }

            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Reading singers exception", e);
        }


        return singersCatalog;
    }

    @Override
    public Singer readSinger(int singerId) {
        Singer singer = null;

        String sql = "SELECT * FROM singer WHERE id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, singerId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                singer = new Singer();

                singer.setId(resultSet.getInt(1));
                singer.setName(resultSet.getString(2));
            }

            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Reading singer exception", e);
        }

        if (singer != null) {
            singer.setAlbums(readAlbums(singerId));
        }


        return singer;
    }

    @Override
    public Duration getDurationOfAllSongsOfSinger(int singerId) {
        Duration duration = Duration.ZERO;

        String sql = "SELECT duration FROM song WHERE singer_id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, singerId);


            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                duration = duration.plus(Duration.parse(resultSet.getString(1)));
            }

            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Reading songs duration exception", e);
        }


        return duration;
    }


    @Override
    public void updateSinger(Singer singer) {
        String sql = "UPDATE singer SET name = ? WHERE id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, singer.getName());
            preparedStatement.setInt(2, singer.getId());


            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Updating singer exception", e);
        }


        ArrayList<Album> albumsWasInDB = (ArrayList<Album>) readAlbums(singer.getId());
        ArrayList<Album> albumsSingerHaveNow = new ArrayList<>(singer.getAlbums());


        if (albumsWasInDB != null) {

            ArrayList<Album> albumsToUpdate = new ArrayList<>();

            for (Album albumWasInDB : albumsWasInDB) {
                albumsToUpdate.addAll(albumsSingerHaveNow.stream()
                        .filter(albumSingerHaveNow -> albumWasInDB.getId() == albumSingerHaveNow.getId())
                        .collect(Collectors.toList()));
            }


            for (Album albumToUpdate : albumsToUpdate) {
                for (int i = 0; i < albumsWasInDB.size(); i++) {
                    if (albumToUpdate.getId() == albumsWasInDB.get(i).getId()) {
                        albumsWasInDB.remove(i);
                        break;
                    }
                }
            }


            if (albumsWasInDB.size() > 0) {
                deleteAlbums(albumsWasInDB);
            }


            if (albumsToUpdate.size() > 0) {
                updateAlbums(albumsToUpdate, singer.getId());


                albumsSingerHaveNow.removeAll(albumsToUpdate);
            }
        }

        if (albumsSingerHaveNow.size() > 0) {
            createAlbums(albumsSingerHaveNow, singer.getId());
        }

    }

    @Override
    public void deleteSinger(int singerId) {
        String sql = "DELETE FROM singer WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, singerId);

            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Deleting singer exception", e);
        }
    }


    private List<Album> readAlbums(int singerId) {
        List<Album> albums = new ArrayList<>();

        String sql = "SELECT * FROM album WHERE singer_id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, singerId);


            ResultSet resultSet = preparedStatement.executeQuery();

            Album album;
            int albumId;

            while (resultSet.next()) {
                album = new Album();
                albumId = resultSet.getInt(1);

                album.setId(albumId);
                album.setName(resultSet.getString(2));
                album.setGenre(resultSet.getString(3));
                album.setSongs(readSongs(albumId));


                albums.add(album);
            }

            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Reading albums error", e);
        }

        if (albums.size() > 0) {
            return albums;
        }

        return null;

    }

    private List<Song> readSongs(int albumId) {
        List<Song> songs = new ArrayList<>();

        String sql = "SELECT * FROM song WHERE album_id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, albumId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Song song;

            while (resultSet.next()) {
                song = new Song();

                song.setId(resultSet.getInt(1));
                song.setTitle(resultSet.getString(2));
                song.setDuration(Duration.parse(resultSet.getString(3)));


                songs.add(song);
            }

            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Reading songs Error", e);
        }

        if (songs.size() > 0) {
            return songs;
        }

        return null;

    }


    private void createAlbums(List<Album> albums, int singerId) {
        String sql = "INSERT INTO album VALUES (?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);


            for (Album album : albums) {

                preparedStatement.setInt(1, album.getId());
                preparedStatement.setString(2, album.getName());
                preparedStatement.setString(3, album.getGenre());
                preparedStatement.setInt(4, singerId);

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Inserting albums exception", e);
        }

        albums.stream().filter(album -> album.getSongs().size() > 0).forEach(album -> {
            createSongs(album.getSongs(), singerId, album.getId());
        });


    }

    private void createSongs(List<Song> songs, int singerId, int albumId) {
        String sql = "INSERT INTO song VALUES (?, ?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Song song : songs) {

                preparedStatement.setInt(1, song.getId());
                preparedStatement.setString(2, song.getTitle());
                preparedStatement.setString(3, song.getDuration().toString());
                preparedStatement.setInt(4, singerId);
                preparedStatement.setInt(5, albumId);


                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Inserting songs exception", e);
        }

    }


    private void updateAlbums(List<Album> albums, int singerId) {

        for (Album album : albums) {

            ArrayList<Song> songsWasInDB = (ArrayList<Song>) readSongs(album.getId());
            ArrayList<Song> songsAlbumHaveNow = new ArrayList<>(album.getSongs());


            if (songsWasInDB != null) {
                ArrayList<Song> songsToUpdate = new ArrayList<>();

                for (Song songWasInDB : songsWasInDB) {
                    songsToUpdate.addAll(songsAlbumHaveNow.stream()
                            .filter(songSingerHaveNow -> songWasInDB.getId() == songSingerHaveNow.getId())
                            .collect(Collectors.toList()));
                }


                for (Song songToUpdate : songsToUpdate) {
                    for (int j = 0; j < songsWasInDB.size(); j++) {
                        if (songToUpdate.getId() == songsWasInDB.get(j).getId()) {
                            songsWasInDB.remove(j);
                            break;
                        }
                    }
                }


                if (songsWasInDB.size() > 0) {
                    deleteSongs(songsWasInDB);
                }

                if (songsToUpdate.size() > 0) {
                    updateSongs(songsToUpdate);

                    songsAlbumHaveNow.removeAll(songsToUpdate);
                }
            }

            if (songsAlbumHaveNow.size() > 0) {
                createSongs(songsAlbumHaveNow, singerId, album.getId());
            }
        }


        String sql = "UPDATE album SET name = ?, genre = ? WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Album album : albums) {

                preparedStatement.setString(1, album.getName());
                preparedStatement.setString(2, album.getGenre());
                preparedStatement.setInt(3, album.getId());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Updating albums exception", e);
        }
    }

    private void updateSongs(List<Song> songs) {
        String sql = "UPDATE song SET title = ?, duration = ? WHERE id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Song song : songs) {

                preparedStatement.setString(1, song.getTitle());
                preparedStatement.setString(2, song.getDuration().toString());
                preparedStatement.setInt(3, song.getId());


                preparedStatement.executeUpdate();
            }

            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Updating songs exception", e);
        }
    }


    private void deleteAlbums(List<Album> albums) {

        albums.stream()
                .filter(album -> album.getSongs().size() > 0 && album.getSongs() != null)
                .forEach(album -> {
                    deleteSongs(album.getSongs());
                });


        String sql = "DELETE FROM album WHERE id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Album album : albums) {
                preparedStatement.setInt(1, album.getId());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Deletion albums exception", e);
        }
    }

    private void deleteSongs(List<Song> songs) {
        String sql = "DELETE FROM song WHERE id = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (Song song : songs) {
                preparedStatement.setInt(1, song.getId());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            LOGGER.error("Deleting songs exception", e);
        }
    }

}
