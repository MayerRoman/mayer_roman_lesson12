package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayer Roman on 05.06.2016.
 */
@XmlRootElement
public class SingersCatalog {
    private List<Singer> singers = new ArrayList<>();

    public List<Singer> getSingers() {
        return singers;
    }

    @XmlElement(name = "singer")
    @XmlElementWrapper
    public void setSingers(List<Singer> singers) {
        this.singers = singers;
    }

    public void addSinger(Singer singer) {
        singers.add(singer);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingersCatalog that = (SingersCatalog) o;

        return singers != null ? singers.equals(that.singers) : that.singers == null;

    }

    @Override
    public int hashCode() {
        return singers != null ? singers.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SingersCatalog{" +
                "singers=" + singers +
                '}';
    }
}
