package dao.impl;

import dao.Migration;
import dao.SingerDAO;

/**
 * Created by Mayer Roman on 14.06.2016.
 */
public class XmlPsqSqlMigration implements Migration {

    @Override
    public void migrate(SingerDAO from, SingerDAO to) {

        to.createSingersFromCatalog(from.readSingersCatalog());
    }
}
