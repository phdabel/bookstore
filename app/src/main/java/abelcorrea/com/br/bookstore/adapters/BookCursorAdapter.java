package abelcorrea.com.br.bookstore.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.activities.MainActivity;
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
    public void bindView(View view, final Context context, Cursor cursor) {


        TextView nameTextView = view.findViewById(R.id.name_list_item);
        TextView priceTextView = view.findViewById(R.id.price_list_item);
        final TextView quantityTextView = view.findViewById(R.id.quantity_list_item);

        Button sellButton = view.findViewById(R.id.sell_button);

        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        final long bookId = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        String price = String.valueOf(cursor.getDouble(priceColumnIndex));
        String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));

        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri queryUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,bookId);
                Cursor query = context.getContentResolver().query(queryUri,new String[]{ProductEntry.COLUMN_PRODUCT_QUANTITY},null,null,null);
                query.moveToFirst();
                int newQuantity = query.getInt(0);
                Uri sell = ContentUris.withAppendedId(ProductEntry.QUANTITY_URI_DOWN,bookId);
                ContentValues value = new ContentValues();
                if(newQuantity > 0){
                    newQuantity--;
                    value.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(sell, value,null,null);
                    quantityTextView.setText(String.valueOf(newQuantity));
                }else
                    Toast.makeText(context, R.string.no_items_for_sale_toasty, Toast.LENGTH_SHORT).show();

            }
        });

    }

}
