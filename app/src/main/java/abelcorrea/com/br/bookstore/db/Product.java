package abelcorrea.com.br.bookstore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.data.BookStoreContract;
import abelcorrea.com.br.bookstore.data.ProductStoreDbHelper;

public class Product extends BaseModel implements IActiveRecord {

    public static final String LOG_TAG = Product.class.getSimpleName();

    private SQLiteOpenHelper dbHelper;
    private Context context;

    public String name;
    public Double price;
    public Integer quantity;
    public String supplierName;
    public String supplierPhone;

    final String[] completeProjection = {
            BookStoreContract.ProductEntry._ID,
            BookStoreContract.ProductEntry.COLUMN_PRODUCT_NAME,
            BookStoreContract.ProductEntry.COLUMN_PRODUCT_PRICE,
            BookStoreContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
            BookStoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            BookStoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
    };

    public Product(Context context){
        this.context = context;
        this.dbHelper = new ProductStoreDbHelper(context);
    }

    @Override
    public long insert() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookStoreContract.ProductEntry.COLUMN_PRODUCT_NAME, this.name);
        values.put(BookStoreContract.ProductEntry.COLUMN_PRODUCT_PRICE, this.price);
        values.put(BookStoreContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, this.quantity);
        values.put(BookStoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, this.supplierName);
        values.put(BookStoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, this.supplierPhone);
        this.id = db.insert(BookStoreContract.ProductEntry.TABLE_NAME, null, values);
        return this.id;
    }

    @Override
    public BaseModel update() {
        return null;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public List<ProductModel> findAll() {
        return this.find(completeProjection, null, null);
    }

    @Override
    public ProductModel findOne(String[] projection, String selection, String[] selectionArgs) {
        List<ProductModel> results = find(projection, selection, selectionArgs);
        if(results.size() > 1) try {
            throw new Exception(context.getString(R.string.exception_message_find_one));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return results.get(0);
    }

    @Override
    public ProductModel findOne(String selection, String[] selectionArgs) {
        List<ProductModel> results = find(completeProjection, selection, selectionArgs);
        if(results.size() > 1) try {
            throw new Exception(context.getString(R.string.exception_message_find_one));
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return results.get(0);
    }

    @Override
    public List<ProductModel> find(String selection, String[] selectionArgs){
        return this.find(completeProjection, selection, selectionArgs);
    }

    @Override
    public List<ProductModel> find(String[] projection, String selection, String[] selectionArgs) {

        List<ProductModel> results = new ArrayList<ProductModel>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                BookStoreContract.ProductEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        try{
            int idColumnIndex = cursor.getColumnIndex(BookStoreContract.ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookStoreContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookStoreContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookStoreContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookStoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookStoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            while(cursor.moveToNext()){
                results.add(new ProductModel(
                        cursor.getLong(idColumnIndex),
                        cursor.getString(nameColumnIndex),
                        cursor.getDouble(priceColumnIndex),
                        cursor.getInt(quantityColumnIndex),
                        cursor.getString(supplierNameColumnIndex),
                        cursor.getString(supplierPhoneColumnIndex)));
            }
        }finally{
            cursor.close();
        }
        return results;
    }
}
