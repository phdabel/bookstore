package abelcorrea.com.br.bookstore.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.data.ProductStoreDbHelper;
import abelcorrea.com.br.bookstore.db.Product;
import abelcorrea.com.br.bookstore.db.ProductModel;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ProductStoreDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ProductStoreDbHelper(this);

        Product p1 = new Product(this);
        p1.name = "Harry Potter and The Philosopher's Stone";
        p1.price = 14.99;
        p1.quantity = 1000;
        p1.supplierName = "Saraiva";
        p1.supplierPhone = "+55 51 3355-4455";
        long id1 = p1.insert();
        Log.d(LOG_TAG, "New Book ID: " + String.valueOf(id1));
        Log.d(LOG_TAG, "New Book Data: " + p1.toString());

        Product p2 = new Product(this);
        p2.name = "A Knight of The Seven Kingdoms";
        p2.price = 29.90;
        p2.quantity = 2000;
        p2.supplierName = "E-Bay";
        p2.supplierPhone = null;
        long id2 = p2.insert();
        Log.d(LOG_TAG, "New Book ID: " + String.valueOf(id2));
        Log.d(LOG_TAG, "New Book Data: " + p2.toString());

        Product p3 = new Product(this);
        p3.name = "Artificial Intelligence: A Modern approach";
        p3.price = 140.00;
        p3.quantity = 400;
        p3.supplierName = "Amazon";
        p3.supplierPhone = null;
        long id3 = p3.insert();
        Log.d(LOG_TAG, "New Book ID: " + String.valueOf(id3));
        Log.d(LOG_TAG, "New Book Data: " + p3.toString());

        String[] args = {
                String.valueOf(20.00)
        };
        ProductModel p4 = p1.findOne("price > ?", args);

        Log.d(LOG_TAG, "Searching Book with ID = 1: " + p4.toString());
    }

}
