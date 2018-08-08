package abelcorrea.com.br.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.widget.Toast;

import abelcorrea.com.br.bookstore.R;

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
        public static final Uri QUANTITY_INCREASE_URI = Uri.withAppendedPath(BASE_CONTENT_UTI, PATH_QUANTITY_UP);
        public static final Uri QUANTITY_DECREASE_URI = Uri.withAppendedPath(BASE_CONTENT_UTI, PATH_QUANTITY_DOWN);

        public static final int MIN_QUANTITY = 0;

        /**
         * MIME types
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * If the new value for the quantity field is valid returns null.
         * Otherwise returns false.
         *
         * @param newValue
         * @return
         */
        public static boolean validateQuantity(int newValue){
            if(newValue >= ProductEntry.MIN_QUANTITY) return true;
            return false;
        }

        /**
         * Validate if the required fields are filled.
         * Returns null if all required fields are filled, otherwise returns
         * the name of the column.
         *
         * @param name name of the book
         * @param price price of the book
         * @param quantity quantity of books
         * @return
         */
        public static String getInvalidRequiredFields(String name, String price, String quantity){
            if (TextUtils.isEmpty(name)) {
                return COLUMN_PRODUCT_NAME;
            } else if (TextUtils.isEmpty(price)) {
                return COLUMN_PRODUCT_PRICE;
            } else if (TextUtils.isEmpty(quantity)) {
                return COLUMN_PRODUCT_QUANTITY;
            }
            return null;
        }

    }

}
