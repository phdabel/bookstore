package abelcorrea.com.br.bookstore.db;

import java.util.List;

public interface IActiveRecord {


    long insert();

    BaseModel update();

    boolean delete();

    BaseModel findOne(String selection, String[] selectionArgs);

    List<? extends BaseModel> findAll();

}
