package dao.impl.Xml;

import dao.DAOFactory;
import dao.SingerDAO;

/**
 * Created by Mayer Roman on 03.06.2016.
 */
public class XmlDaoFactory implements DAOFactory {

    @Override
    public SingerDAO getSingerDAO() {
        return new XmlSingerDAO();
    }
}
