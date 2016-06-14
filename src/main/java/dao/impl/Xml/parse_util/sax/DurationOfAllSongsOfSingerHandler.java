package dao.impl.Xml.parse_util.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.Duration;

/**
 * Created by Mayer Roman on 07.06.2016.
 */
public class DurationOfAllSongsOfSingerHandler extends DefaultHandler {
    private Duration totalDurationOfAllSongsOfSinger;

    private int expectedSingerID;
    private boolean expectedSingerFound = false;

    private String tagName;


    public DurationOfAllSongsOfSingerHandler(int expectedSingerID) {
        this.expectedSingerID = expectedSingerID;
    }

    public Duration getTotalDurationOfAllSongsOfSinger() {
        return totalDurationOfAllSongsOfSinger;
    }

    @Override
    public void startDocument() throws SAXException {
        totalDurationOfAllSongsOfSinger = Duration.ZERO;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagName = qName;

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (tagName.equals("id")) {

            int singerId = Integer.parseInt(new String(ch, start, length));

            if (expectedSingerID == singerId) {
                expectedSingerFound = true;
            }
        }

        if (expectedSingerFound && tagName.equals("duration")) {

            totalDurationOfAllSongsOfSinger =
                    totalDurationOfAllSongsOfSinger.plus(Duration.parse(new String(ch, start, length)));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (expectedSingerFound && qName.equals("singer")) {
            expectedSingerFound = false;
        }
    }

}
