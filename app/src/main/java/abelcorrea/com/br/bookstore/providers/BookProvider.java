package abelcorrea.com.br.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.data.BookStoreContract;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;
import abelcorrea.com.br.bookstore.data.ProductStoreDbHelper;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private ProductStoreDbHelper dbHelper;

    private static final int BOOKS = 000;

    private static final int BOOK_ID = 001;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, "books", BOOKS);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY,"books/#", BOOK_ID);
    }


    @Override
    public boolean onCreate() {
        this.dbHelper = new ProductStoreDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                cursor = db.query(ProductEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,sortOrder);

                break;
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.query_unknown_uri) + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case BOOKS:
                Uri insert = insertItem(uri, contentValues);
                getContext().getContentResolver().notifyChange(uri, null);
                return insert;
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);

        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int update = -1;
        switch (match){
            case BOOKS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                update = updateItem(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if(update != 0) getContext().getContentResolver().notifyChange(uri, null);
        return update;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(getContext().getString(R.string.get_type_string_1)
                        + uri
                        + getContext().getString(R.string.get_type_string_2)
                        + match);
        }
    }

    private Uri insertItem(Uri uri, ContentValues contentValues){

        validate(contentValues);
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        validate(contentValues);
        if(contentValues.size() == 0) return 0;
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        return db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    private void validate(ContentValues values){
        String itemName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        Double itemPrice = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        Integer itemQuantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        if(itemName == null || itemName.isEmpty()) throw new IllegalArgumentException("The name of the product must be informed.");
        if(itemPrice == null || itemPrice.doubleValue() < 0) throw new IllegalArgumentException("The price should be a positive value.");
        if(itemQuantity == null || itemQuantity.intValue() < 0) throw new IllegalArgumentException("The quantity can not be empty or a negative value.");

    }
}
