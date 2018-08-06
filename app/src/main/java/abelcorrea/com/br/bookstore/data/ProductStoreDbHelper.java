package abelcorrea.com.br.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;

public class ProductStoreDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductStoreDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "store.db";

    private static final int DATABASE_VERSION = 2;

    public ProductStoreDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.createDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.dropDB(db);
        this.createDB(db);
    }

    private void createDB(SQLiteDatabase db){
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NULL, "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT NULL,"
                + ProductEntry.COLUMN_PRODUCT_GENRE + " TEXT NULL);";

        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    private void dropDB(SQLiteDatabase db){
        String SQL_DROP_PRODUCTS_TABLE = "DROP TABLE " + ProductEntry.TABLE_NAME + ";";
        db.execSQL(SQL_DROP_PRODUCTS_TABLE);
    }

}
