package abelcorrea.com.br.bookstore.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.activities.MainActivity;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.name_edit_text) EditText nameEditText;
    @BindView(R.id.price_edit_text) EditText priceEditText;
    @BindView(R.id.quantity_edit_text) EditText quantityEditText;
    @BindView(R.id.supplier_name_edit_text) EditText supplierNameEditText;
    @BindView(R.id.supplier_phone_edit_text) EditText supplierPhoneEditText;

    private final int EXISTING_ITEM_LOADER = 0;

    private boolean mItemHasChanged = false;

    private Uri currentUri;

    public EditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        currentUri = intent.getData();

        View view = inflater.inflate(R.layout.fragment_editor, container, false);
        ButterKnife.bind(this,view);

        if(currentUri == null){
            getActivity().setTitle(getString(R.string.add_item_label));
            getActivity().invalidateOptionsMenu();
        }else{
            getActivity().setTitle(getString(R.string.edit_item_label));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        ((MainActivity)getActivity()).setOnBackPressedListener(new OnBackPressedListener(){
            @Override
            public void doBack(){
                currentUri = null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new InventoryFragment())
                        .commit();
            }
        });

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_editor, menu);
        if(currentUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(getContext(),
                currentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if(cursor == null || cursor.getCount() < 1) return;

        if(cursor.moveToFirst()){

            int nameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);


            String name = cursor.getString(nameIndex);
            Double price = cursor.getDouble(priceIndex);
            Integer quantity = cursor.getInt(quantityIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            String supplierPhone = cursor.getString(supplierPhoneIndex);

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        nameEditText.setText(null);
        priceEditText.setText(null);
        quantityEditText.setText(null);
        supplierNameEditText.setText(null);
        supplierPhoneEditText.setText(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                saveItem();
                getActivity().getIntent().setData(null);
                getFragmentManager().beginTransaction().
                        replace(R.id.frame_layout, new InventoryFragment()).commit();
                return true;
            case R.id.action_delete:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItem(){

        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        if(currentUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) return;

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Double.valueOf(priceString));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.valueOf(quantityString));
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        if(currentUri == null){
            Uri newUri = getContext().getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if(newUri == null){
                Toast.makeText(getContext(), R.string.error_saving_label, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), R.string.saved_item_label, Toast.LENGTH_SHORT).show();
            }

        }else{
            int updatedRows = getContext().getContentResolver().update(currentUri, values, null, null);

            if(updatedRows == 0){
                Toast.makeText(getContext(), R.string.error_updating_label, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), R.string.item_updated_label, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
