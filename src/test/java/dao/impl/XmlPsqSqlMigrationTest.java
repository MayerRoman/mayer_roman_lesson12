package dao.impl;

import dao.DAOFactory;
import dao.Migration;
import dao.SingerDAO;
import dao.impl.PsgSql.PsgSqlDaoFactory;
import dao.impl.Xml.XmlDaoFactory;
import model.Album;
import model.Singer;
import model.SingersCatalog;
import model.Song;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * Created by Mayer Roman on 14.06.2016.
 */
public class XmlPsqSqlMigrationTest {
    private static SingerDAO xmlSingerDAO;
    private static SingerDAO psgSqlSingerDAO;
    private static SingersCatalog singersCatalog;

    @BeforeClass
    public static void prepareDaoAndSingersCatalog() {
        DAOFactory daoFactory = new XmlDaoFactory();
        xmlSingerDAO = daoFactory.getSingerDAO();

        daoFactory = new PsgSqlDaoFactory();
        psgSqlSingerDAO = daoFactory.getSingerDAO();

        singersCatalog = prepareSingersCatalog();
    }

    @AfterClass
    public static void removeReferences() {
        xmlSingerDAO = null;
        psgSqlSingerDAO = null;
        singersCatalog = null;
    }

    @Test
    public void migrate() throws Exception {
        SingersCatalog singersCatalogBeforeMigration = singersCatalog;

        Migration xmlToPsgSqlMigration = new XmlPsqSqlMigration();
        xmlToPsgSqlMigration.migrate(xmlSingerDAO, psgSqlSingerDAO);

        SingersCatalog singersCatalogAfterMigration = psgSqlSingerDAO.readSingersCatalog();

        assertEquals(singersCatalogAfterMigration, singersCatalogBeforeMigration);
    }

    private static SingersCatalog prepareSingersCatalog() {
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