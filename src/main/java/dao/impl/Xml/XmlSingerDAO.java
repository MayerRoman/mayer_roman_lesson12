package dao.impl.Xml;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import dao.SingerDAO;
import dao.impl.Xml.parse_util.jaxb.DurationAdapter;
import dao.impl.Xml.parse_util.sax.DurationOfAllSongsOfSingerHandler;
import dao.impl.Xml.parse_util.sax.FindAllSingersHandler;
import dao.impl.Xml.parse_util.sax.FindSingerByIdHandler;
import model.Album;
import model.Singer;
import model.SingersCatalog;
import model.Song;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

/**
 * Created by Mayer Roman on 03.06.2016.
 */
public class XmlSingerDAO implements SingerDAO {
    private static final Logger LOGGER = LogManager.getLogger(XmlSingerDAO.class);

    private static final File XML_MUSIC_CATALOG_FILE = new File("SingersCatalog.xml");


    @Override
    public void createSingersFromCatalog(SingersCatalog catalog) {
        JAXBContext context;
        Marshaller marshaller;

        try {
            context = JAXBContext.newInstance(SingersCatalog.class);
            marshaller = context.createMarshaller();

            marshaller.setAdapter(DurationAdapter.class, new DurationAdapter());
            marshaller.marshal(catalog, XML_MUSIC_CATALOG_FILE);

        } catch (JAXBException e) {
            LOGGER.error("Exception while writing in XML file with JAXB", e);
        }

    }

    @Override
    public void createSinger(Singer singer) {
        try {
            Document document = getDocumentTreeFromXML();
            Element rootElement = document.getDocumentElement();

            Element singersElement = getSingleChild(rootElement, "singers");
            singersElement.appendChild(createSingerElement(singer, document));

            writeChangesInXML(document);

        } catch (IOException | SAXException | TransformerException e) {
            LOGGER.error("Exception while writing in XML file with DOM", e);
        }

    }


    @Override
    public SingersCatalog readSingersCatalog() {
        SingersCatalog catalog = null;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            FindAllSingersHandler handler = new FindAllSingersHandler();
            saxParser.parse(XML_MUSIC_CATALOG_FILE, handler);

            catalog = handler.getSingersCatalog();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("Exception while reading XML file with SAX", e);
        }


        return catalog;
    }

    @Override
    public Singer readSinger(int singerId) {
        Singer singer = null;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            FindSingerByIdHandler handler = new FindSingerByIdHandler(singerId);
            saxParser.parse(XML_MUSIC_CATALOG_FILE, handler);

            singer = handler.getSinger();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("Exception while reading XML file with SAX", e);
        }


        return singer;
    }

    @Override
    public Duration getDurationOfAllSongsOfSinger(int singerId) {
        Duration duration = null;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DurationOfAllSongsOfSingerHandler handler = new DurationOfAllSongsOfSingerHandler(singerId);
            saxParser.parse(XML_MUSIC_CATALOG_FILE, handler);

            duration = handler.getTotalDurationOfAllSongsOfSinger();

        } catch (SAXException | ParserConfigurationException | IOException e) {
            LOGGER.error("Exception while reading XML file with SAX", e);
        }

        return duration;
    }


    @Override
    public void updateSinger(Singer singer) {
        try {
            Document document = getDocumentTreeFromXML();
            Element rootElement = document.getDocumentElement();

            NodeList singerNodes = rootElement.getElementsByTagName("singer");
            Element oldSinger = findSingerElementById(singerNodes, singer.getId());
            Element newSinger = createSingerElement(singer, document);

            Element singersElement = getSingleChild(rootElement, "singers");
            singersElement.replaceChild(newSinger, oldSinger);

            writeChangesInXML(document);

        } catch (SAXException | IOException | TransformerException e) {
            LOGGER.error("Exception while writing in XML file with DOM", e);
        }

    }

    @Override
    public void deleteSinger(int singerId) {

        try {
            Document document = getDocumentTreeFromXML();
            Element rootElement = document.getDocumentElement();

            NodeList singerNodes = rootElement.getElementsByTagName("singer");
            Element expectedSingerElement = findSingerElementById(singerNodes, singerId);

            Node singerParentNode = expectedSingerElement.getParentNode();
            singerParentNode.removeChild(expectedSingerElement);

            writeChangesInXML(document);

        } catch (SAXException | IOException | TransformerException e) {
            LOGGER.error("Exception while changing XML file with DOM", e);
        }

    }



    private static Document getDocumentTreeFromXML() throws IOException, SAXException {
        DOMParser parser = new DOMParser();
        parser.parse(XML_MUSIC_CATALOG_FILE.toString());

        return parser.getDocument();
    }


    private static Element createSingerElement(Singer singer, Document document) {
        Element singerElement = document.createElement("singer");

        Element singerIdElement = document.createElement("id");
        singerIdElement.setTextContent(String.valueOf(singer.getId()));
        singerElement.appendChild(singerIdElement);

        Element singerNameElement = document.createElement("name");
        singerNameElement.setTextContent(singer.getName());
        singerElement.appendChild(singerNameElement);

        Element albumsElement = document.createElement("albums");
        singerElement.appendChild(albumsElement);

        singer.getAlbums().stream()
                .forEach((album) -> albumsElement.appendChild(createAlbumElement(album, document)));

        return singerElement;

    }

    private static Element createAlbumElement(Album album, Document document) {
        Element albumElement = document.createElement("album");

        Element albumIdElement = document.createElement("albumId");
        albumIdElement.setTextContent(String.valueOf(album.getId()));
        albumElement.appendChild(albumIdElement);

        Element albumNameElement = document.createElement("albumName");
        albumNameElement.setTextContent(album.getName());
        albumElement.appendChild(albumNameElement);

        Element albumGenreElement = document.createElement("genre");
        albumGenreElement.setTextContent(album.getGenre());
        albumElement.appendChild(albumGenreElement);

        Element songsElement = document.createElement("songs");
        albumElement.appendChild(songsElement);

        album.getSongs().stream()
                .forEach((song) -> songsElement.appendChild(createSongElement(song, document)));

        return albumElement;
    }

    private static Element createSongElement(Song song, Document document) {
        Element songElement = document.createElement("song");

        Element songIdElement = document.createElement("songId");
        songIdElement.setTextContent(String.valueOf(song.getId()));
        songElement.appendChild(songIdElement);

        Element songTitleElement = document.createElement("title");
        songTitleElement.setTextContent(song.getTitle());
        songElement.appendChild(songTitleElement);

        Element songDurationElement = document.createElement("duration");
        songDurationElement.setTextContent(song.getDuration().toString());
        songElement.appendChild(songDurationElement);


        return songElement;

    }


    private static Element getSingleChild(Element parentElement, String childName) {
        NodeList nodeList = parentElement.getElementsByTagName(childName);
        Element child = (Element) nodeList.item(0);

        return child;
    }

    private static Element findSingerElementById(NodeList singerNodes, int singerID) {
        Element expectedSingerElement = null;

        for (int i = 0; i < singerNodes.getLength(); i++) {
            expectedSingerElement = (Element) singerNodes.item(i);
            NodeList nodeList = expectedSingerElement.getElementsByTagName("id");

            Element singerId = (Element) nodeList.item(0);
            if (singerID == Integer.parseInt(singerId.getTextContent())) {
                return expectedSingerElement;
            }
        }

        return expectedSingerElement;
    }

    private static void writeChangesInXML(Document document) throws TransformerException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult((new FileWriter(XML_MUSIC_CATALOG_FILE.toString())));

        transformer.transform(source, result);
    }


}
