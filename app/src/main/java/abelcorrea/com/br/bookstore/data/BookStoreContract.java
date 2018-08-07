package abelcorrea.com.br.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookStoreContract {

    public static final String CONTENT_AUTHORITY = "abelcorrea.com.br.bookstore";

    public static final Uri BASE_CONTENT_UTI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

    public static final String PATH_QUANTITY_UP = "quantity/up";

    public static final String PATH_QUANTITY_DOWN = "quantity/down";


    private BookStoreContract(){}

    public static final class ProductEntry implements BaseColumns {

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_GENRE = "genre";
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_UTI, PATH_BOOKS);
        public static final Uri QUANTITY_URI_UP = Uri.withAppendedPath(BASE_CONTENT_UTI, PATH_QUANTITY_UP);
        public static final Uri QUANTITY_URI_DOWN = Uri.withAppendedPath(BASE_CONTENT_UTI, PATH_QUANTITY_DOWN);

        /**
         * MIME types
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static boolean validQuantity(int quantity){
            if(quantity < 0) return false;
            return true;
        }


    }

}
