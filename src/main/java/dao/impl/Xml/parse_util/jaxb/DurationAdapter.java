package dao.impl.Xml.parse_util.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

/**
 * Created by Mayer Roman on 07.06.2016.
 */
public class DurationAdapter extends XmlAdapter<Duration, java.time.Duration> {
    @Override
    public java.time.Duration unmarshal(Duration v) throws Exception {
        return java.time.Duration.parse(v.toString());
    }

    @Override
    public Duration marshal(java.time.Duration v) throws Exception {
        return DatatypeFactory.newInstance().newDuration(v.toString());
    }
}
