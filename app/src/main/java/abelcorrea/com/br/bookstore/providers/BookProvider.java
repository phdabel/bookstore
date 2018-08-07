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
import android.widget.Toast;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.data.BookStoreContract;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;
import abelcorrea.com.br.bookstore.data.ProductStoreDbHelper;
import abelcorrea.com.br.bookstore.data.ValidationException;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    private ProductStoreDbHelper dbHelper;

    private static final int BOOKS = 000;

    private static final int BOOK_ID = 001;

    private static final int QUANTITY_UP = 111;

    private static final int QUANTITY_DOWN = 110;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, "books", BOOKS);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY,"books/#", BOOK_ID);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY,"quantity/up/#",QUANTITY_UP);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY,"quantity/down/#",QUANTITY_DOWN);
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
                throw new IllegalArgumentException(getContext().getString(R.string.illegal_argument_query_unknown_uri) + uri);
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
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int delete = -1;

        switch(match){
            case BOOKS:
                delete = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                delete = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(delete != 0) getContext().getContentResolver().notifyChange(uri, null);
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int update = -1;
        switch (match){
            case BOOKS:
                return updateItem(uri, contentValues, selection, selectionArgs, true);
            case BOOK_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri))};
                update = updateItem(uri, contentValues, selection, selectionArgs, true);
                break;
            case QUANTITY_DOWN:
            case QUANTITY_UP:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updateItem(uri, contentValues, selection, selectionArgs, false);
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
            case QUANTITY_UP:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case QUANTITY_DOWN:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(
                        getContext().getString(R.string.illegal_state_get_type)
                                .replace("{uri}",uri.toString())
                                .replace("{match}", String.valueOf(match)));
        }
    }

    private Uri insertItem(Uri uri, ContentValues contentValues){
        try{
            validate(contentValues);
            SQLiteDatabase db = this.dbHelper.getWritableDatabase();
            long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);
            if(id == -1){
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }catch(ValidationException ex){
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private int updateItem(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs, boolean runValidation){
        if(runValidation) validate(contentValues);
        if(contentValues.size() == 0) return 0;
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        return db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }

    private void validate(ContentValues values){
        String itemName = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        Double itemPrice = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        Integer itemQuantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        if(itemName == null || itemName.isEmpty()) throw new ValidationException(getContext().getString(R.string.name_field_validation));
        if(itemPrice == null || itemPrice.doubleValue() < 0) throw new ValidationException(getContext().getString(R.string.price_field_validation));
        if(itemQuantity == null || itemQuantity.intValue() < 0) throw new ValidationException(getContext().getString(R.string.quantity_field_validation));

    }
}
