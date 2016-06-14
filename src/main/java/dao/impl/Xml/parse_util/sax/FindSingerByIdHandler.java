package dao.impl.Xml.parse_util.sax;

import model.Album;
import model.Singer;
import model.Song;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.Duration;

/**
 * Created by Mayer Roman on 06.06.2016.
 */
public class FindSingerByIdHandler extends DefaultHandler{
    private Singer singer;
    private Album album;
    private Song song;

    private int expectedSingerID;
    private boolean expectedSingerFound = false;
    private boolean expectedSingerWasFound = false;

    private String tagName;


    public FindSingerByIdHandler(int expectedSingerID) {
        this.expectedSingerID = expectedSingerID;
    }

    public Singer getSinger() {
        return singer;
    }


    @Override
    public void startDocument() throws SAXException {
        singer = new Singer();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagName = qName;

        if (expectedSingerFound) {
            switch (tagName) {
                case "album":
                    album = new Album();
                    break;
                case "song":
                    song = new Song();
                    break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (tagName.equals("id")) {

            int id = Integer.parseInt(new String(ch, start, length));

            if (expectedSingerID == id) {
                expectedSingerFound = true;
                singer.setId(id);
            }
        }
        if (expectedSingerFound) {
            switch (tagName) {
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
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (expectedSingerFound) {
            switch (qName) {
                case "singer":
                    expectedSingerFound = false;
                    expectedSingerWasFound = true;
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

    @Override
    public void endDocument() throws SAXException {
        if (!expectedSingerWasFound) {
            singer = null;
        }
    }
}
