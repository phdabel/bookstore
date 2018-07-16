package abelcorrea.com.br.bookstore.db;

import java.util.List;

public interface IActiveRecord {


    long insert();

    BaseModel update();

    boolean delete();

    List<? extends BaseModel> find(String selection, String[] selecionArgs);

    List<? extends BaseModel> find(String[] projection, String selection, String[] selectionArgs);

    List<? extends BaseModel> findAll();

    BaseModel findOne(String[] projection, String selection, String[] selectionArgs);

    BaseModel findOne(String selection, String[] selectionArgs);

}
