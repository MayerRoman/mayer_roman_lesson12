package dao;

/**
 * Created by Mayer Roman on 14.06.2016.
 */
public interface Migration {

    void migrate(SingerDAO from, SingerDAO to);
}
