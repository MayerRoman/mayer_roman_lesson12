package dao.impl.Xml.parse_util.sax;

import model.Album;
import model.Singer;
import model.SingersCatalog;
import model.Song;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.Duration;

/**
 * Created by Mayer Roman on 04.06.2016.
 */
public class FindAllSingersHandler extends DefaultHandler {
    private SingersCatalog singersCatalog;

    private Singer singer;
    private Album album;
    private Song song;

    private String tagName;

    public SingersCatalog getSingersCatalog() {
        return singersCatalog;
    }


    @Override
    public void startDocument() throws SAXException {
        singersCatalog = new SingersCatalog();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagName = qName;
        switch (tagName) {
            case "singer":
                singer = new Singer();
                break;
            case "album":
                album = new Album();
                break;
            case "song":
                song = new Song();
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        switch (tagName) {
            case "id":
                singer.setId(Integer.parseInt(new String(ch, start, length)));
                break;
            case "name":
                singer.setName(new String(ch, start, length));
                break;

            case "albumId":
                album.setId(Integer.parseInt(new String(ch, start, length)));
                break;
            case "albumName":
                album.setName(new String(ch, start, length));
                break;
            case "genre":
                album.setGenre(new String(ch, start, length));
                break;

            case "songId":
                song.setId(Integer.parseInt(new String(ch, start, length)));
                break;
            case "title":
                song.setTitle(new String(ch, start, length));
                break;
            case "duration":
                song.setDuration(Duration.parse(new String(ch, start, length)));
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "singer":
                singersCatalog.addSinger(singer);
                break;
            case "album":
                singer.setAlbum(album);
                break;
            case "song":
                album.setSong(song);
                break;
        }
    }


}
