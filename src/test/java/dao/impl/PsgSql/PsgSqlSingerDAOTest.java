package dao.impl.PsgSql;

import dao.DAOFactory;
import dao.SingerDAO;
import model.Album;
import model.Singer;
import model.SingersCatalog;
import model.Song;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by Mayer Roman on 13.06.2016.
 */
public class PsgSqlSingerDAOTest {
    private static SingerDAO singerDAO;

    @BeforeClass
    public static void createSingerDAO() {
        DAOFactory daoFactory = new PsgSqlDaoFactory();
        singerDAO = daoFactory.getSingerDAO();
    }

    @AfterClass
    public static void removeReferenceToSingerDAO() {
        singerDAO = null;
    }




    @Test
    public void createSingersFromCatalog() throws Exception {

        SingersCatalog singersCatalogBeforeSaving = prepareSingersCatalog();

        try {
            singerDAO.createSingersFromCatalog(singersCatalogBeforeSaving);

            SingersCatalog singersCatalogAfterLoading = singerDAO.readSingersCatalog();

            assertEquals(singersCatalogAfterLoading, singersCatalogBeforeSaving);

        } finally {
            singersCatalogBeforeSaving
                    .getSingers().stream()
                    .forEach(singer -> singerDAO.deleteSinger(singer.getId()));
        }

    }

    @Test
    public void createSinger() throws Exception {

        Singer singerBeforeSaving = prepareSinger();
        int singerId = singerBeforeSaving.getId();

        try {
            singerDAO.createSinger(singerBeforeSaving);

            Singer singerAfterLoading = singerDAO.readSinger(singerId);

            assertEquals(singerAfterLoading, singerBeforeSaving);

        } finally {
            singerDAO.deleteSinger(singerId);
        }

    }



    @Test
    public void readSingersCatalog() throws Exception {

        SingersCatalog singersCatalogBeforeSaving = prepareSingersCatalog();

        try {
            singerDAO.createSingersFromCatalog(singersCatalogBeforeSaving);

            SingersCatalog singersCatalogAfterLoading = singerDAO.readSingersCatalog();

            assertEquals(singersCatalogAfterLoading, singersCatalogBeforeSaving);

        } finally {
            singersCatalogBeforeSaving
                    .getSingers().stream()
                    .forEach(singer -> singerDAO.deleteSinger(singer.getId()));
        }
    }

    @Test
    public void readSinger() throws Exception {

        Singer singerBeforeSaving = prepareSinger();
        int singerId = singerBeforeSaving.getId();

        try {
            singerDAO.createSinger(singerBeforeSaving);

            Singer singerAfterLoading = singerDAO.readSinger(singerId);

            assertEquals(singerAfterLoading, singerBeforeSaving);

        } finally {
            singerDAO.deleteSinger(singerId);
        }

    }

    @Test
    public void getDurationOfAllSongsOfSinger() throws Exception {
        Singer singer = prepareSinger();
        int singerId = singer.getId();

        Duration durationBeforeSaving = Duration.ZERO;
        List<Album> albumList = singer.getAlbums();
        for (Album album : albumList) {
            for (Song song : album.getSongs()) {
                durationBeforeSaving = durationBeforeSaving.plus(song.getDuration());
            }
        }

        try {
            singerDAO.createSinger(singer);

            Duration durationAfterReading = singerDAO.getDurationOfAllSongsOfSinger(singerId);

            assertEquals(durationAfterReading, durationBeforeSaving);
            
        } finally {
            singerDAO.deleteSinger(singerId);
        }

    }



    @Test
    public void updateSinger() throws Exception {

        SingersCatalog singersCatalog = prepareSingersCatalog();

        Singer singerToUpdate = prepareSinger();
        int singerId = singerToUpdate.getId();

        try {
            singerDAO.createSingersFromCatalog(singersCatalog);

            singerDAO.updateSinger(singerToUpdate);

            Singer singerAfterUpdating = singerDAO.readSinger(singerId);

            assertEquals(singerAfterUpdating, singerToUpdate);

        } finally {

            singersCatalog
                    .getSingers().stream()
                    .forEach(singer -> singerDAO.deleteSinger(singer.getId()));
        }

    }

    @Test
    public void deleteSinger() throws Exception {

        Singer singer = prepareSinger();
        int singerID = singer.getId();

        singerDAO.createSinger(singer);

        singerDAO.deleteSinger(singerID);

        Singer expectedNull = singerDAO.readSinger(singerID);
        assertNull(expectedNull);
    }





    private Singer prepareSinger() {
        Singer singer = new Singer(1, "TestSinger");

        Album album1 = new Album(1, "TestAlbum", "TestGenre");
        singer.setAlbum(album1);

        Song song = new Song(1, "TestSong1", Duration.ofMinutes(4).plusSeconds(45));
        album1.setSong(song);
        song = new Song(2, "TestSong2", Duration.ofMinutes(3).plusSeconds(34));
        album1.setSong(song);


        return singer;
    }

    private SingersCatalog prepareSingersCatalog() {
        SingersCatalog singersCatalog = new SingersCatalog();
        Singer u2 = new Singer(1, "U2");
        Singer sting = new Singer(2, "Sting");
        Singer acDc = new Singer(3, "AC/DC");

        Album allThatYouCantLeaveBehind = new Album(1, "All that you can't left behind", "Pop-Rock");
        Album howToDismantleAnAtomicBomb = new Album(2, "How To Dismantle An Atomic Bomb", "Rock");
        Album brandNewDay = new Album(3, "Brand New Day", "Pop");
        Album whoMadeWho = new Album(4, "Who Made Who", "Rock");

        Song elevation = new Song(1, "Elevation", Duration.ofMinutes(3).plusSeconds(46));
        Song walkOn = new Song(2, "Walk On", Duration.ofMinutes(4).plusSeconds(56));
        Song vertigo = new Song(3, "Vertigo", Duration.ofMinutes(3).plusSeconds(13));
        Song desertRose = new Song(4, "Desert Rose", Duration.ofMinutes(4).plusSeconds(47));
        Song forThoseAboutToRock = new Song(5, "For Those About To Rock", Duration.ofMinutes(5).plusSeconds(53));

        u2.setAlbum(allThatYouCantLeaveBehind);
        u2.setAlbum(howToDismantleAnAtomicBomb);
        sting.setAlbum(brandNewDay);
        acDc.setAlbum(whoMadeWho);

        allThatYouCantLeaveBehind.setSong(elevation);
        allThatYouCantLeaveBehind.setSong(walkOn);
        howToDismantleAnAtomicBomb.setSong(vertigo);
        brandNewDay.setSong(desertRose);
        whoMadeWho.setSong(forThoseAboutToRock);

        singersCatalog.addSinger(u2);
        singersCatalog.addSinger(sting);
        singersCatalog.addSinger(acDc);


        return singersCatalog;
    }


}