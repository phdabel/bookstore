package abelcorrea.com.br.bookstore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;

public class BookCursorAdapter extends CursorAdapter {


    public BookCursorAdapter(Context context, Cursor c){
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = view.findViewById(R.id.name_list_item);
        TextView priceTextView = view.findViewById(R.id.price_list_item);
        TextView quantityTextView = view.findViewById(R.id.quantity_list_item);

        Button sellButton = view.findViewById(R.id.sell);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        String price = String.valueOf(cursor.getDouble(priceColumnIndex));
        String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));

        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);

    }
}
