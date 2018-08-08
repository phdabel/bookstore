package abelcorrea.com.br.bookstore.fragments;


import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.activities.MainActivity;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;
import abelcorrea.com.br.bookstore.filters.CurrencyInputFilter;
import abelcorrea.com.br.bookstore.listeners.OnBackPressedListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorFragment.class.getSimpleName();

    private static final int CALL_SUPPLIER_PHONE = 0;

    @NonNull
    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @Nullable
    @BindView(R.id.genre_edit_text)
    EditText genreEditText;
    @NonNull
    @BindView(R.id.price_edit_text)
    EditText priceEditText;
    @NonNull
    @BindView(R.id.quantity_edit_text)
    EditText quantityEditText;
    @Nullable
    @BindView(R.id.supplier_name_edit_text) EditText supplierNameEditText;
    @Nullable
    @BindView(R.id.supplier_phone_edit_text) EditText supplierPhoneEditText;

    @BindView(R.id.quantity_up_image_button) ImageButton qtyUImageButton;
    @BindView(R.id.quantity_down_image_button) ImageButton qtyDImageButton;

    private final int EXISTING_ITEM_LOADER = 0;

    protected boolean mItemHasChanged = false;

    private Uri currentUri;

    private boolean viewMode = true;

    public EditorFragment() {

    }


    private final View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motioEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    private DialogInterface.OnClickListener discardButon = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mItemHasChanged = false;
            getActivity().getIntent().setData(null);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new InventoryFragment())
                    .commit();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        currentUri = intent.getData();
        if (currentUri == null) viewMode = false;
        else if (getArguments() != null)
            this.viewMode = Boolean.parseBoolean(getArguments().getString("viewMode"));
        else this.viewMode = true;

        View view = inflater.inflate(R.layout.fragment_editor, container, false);

        EditText nameEditText = view.findViewById(R.id.name_edit_text);
        EditText genreEditText = view.findViewById(R.id.genre_edit_text);
        EditText priceEditText = view.findViewById(R.id.price_edit_text);
        final EditText quantityEditText = view.findViewById(R.id.quantity_edit_text);
        EditText supplierEditText = view.findViewById(R.id.supplier_name_edit_text);
        final EditText phoneEditText = view.findViewById(R.id.supplier_phone_edit_text);
        final FloatingActionButton phoneImageButton = view.findViewById(R.id.supplier_phone_fab);
        final ImageButton quantityUpButton = view.findViewById(R.id.quantity_up_image_button);
        final ImageButton quantityDownButton = view.findViewById(R.id.quantity_down_image_button);

        quantityUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String quantifier = sharedPrefs.getString(
                        getContext().getString(R.string.settings_quantifier_key),
                        getContext().getString(R.string.settings_quantifier_default));

                long bookId = ContentUris.parseId(currentUri);
                Uri queryUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,bookId);
                Cursor query = getContext().getContentResolver()
                        .query(queryUri,new String[]{ProductEntry.COLUMN_PRODUCT_QUANTITY},null,null,null);
                query.moveToFirst();
                int newQuantity = query.getInt(0);
                Uri buy = ContentUris.withAppendedId(ProductEntry.QUANTITY_INCREASE_URI,bookId);
                ContentValues value = new ContentValues();
                newQuantity += Integer.valueOf(quantifier);
                value.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                getContext().getContentResolver().update(buy, value,null,null);
                quantityEditText.setText(String.valueOf(newQuantity));
            }
        });

        quantityDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String quantifier = sharedPrefs.getString(
                        getContext().getString(R.string.settings_quantifier_key),
                        getContext().getString(R.string.settings_quantifier_default));

                long bookId = ContentUris.parseId(currentUri);
                Uri queryUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,bookId);
                Cursor query = getContext().getContentResolver()
                        .query(queryUri,new String[]{ProductEntry.COLUMN_PRODUCT_QUANTITY},null,null,null);
                query.moveToFirst();
                int newQuantity = query.getInt(0);
                Uri sell = ContentUris.withAppendedId(ProductEntry.QUANTITY_DECREASE_URI,bookId);
                ContentValues value = new ContentValues();
                if((newQuantity -= Integer.valueOf(quantifier)) >= 0){
                    value.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    getContext().getContentResolver().update(sell, value,null,null);
                    quantityEditText.setText(String.valueOf(newQuantity));
                }else
                    Toast.makeText(getContext(), R.string.no_items_for_sale_toasty, Toast.LENGTH_SHORT).show();
            }
        });

        phoneImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneEditText.getText()));
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.CALL_PHONE};
                    ActivityCompat.requestPermissions(getActivity(), permissions, CALL_SUPPLIER_PHONE);
                    phoneImageButton.callOnClick();
                } else
                    getContext().startActivity(intent);
            }
        });

        priceEditText.setFilters(new InputFilter[]{new CurrencyInputFilter()});

        nameEditText.setOnTouchListener(listener);
        genreEditText.setOnTouchListener(listener);
        priceEditText.setOnTouchListener(listener);
        quantityEditText.setOnTouchListener(listener);
        supplierEditText.setOnTouchListener(listener);
        phoneEditText.setOnTouchListener(listener);

        if (viewMode) {
            phoneImageButton.setVisibility(View.VISIBLE);
            nameEditText.setEnabled(false);
            genreEditText.setEnabled(false);
            priceEditText.setEnabled(false);
            quantityEditText.setEnabled(false);
            supplierEditText.setEnabled(false);
            phoneEditText.setEnabled(false);
            quantityDownButton.setVisibility(View.VISIBLE);
            quantityUpButton.setVisibility(View.VISIBLE);
        } else if (!viewMode || currentUri == null) {
            phoneImageButton.setVisibility(View.GONE);
            nameEditText.setEnabled(true);
            genreEditText.setEnabled(true);
            priceEditText.setEnabled(true);
            quantityEditText.setEnabled(true);
            supplierEditText.setEnabled(true);
            phoneEditText.setEnabled(true);
            quantityDownButton.setVisibility(View.GONE);
            quantityUpButton.setVisibility(View.GONE);
        }

        ButterKnife.bind(this, view);


        if (currentUri == null) {
            getActivity().setTitle(getString(R.string.add_book_title));
            getActivity().invalidateOptionsMenu();
        } else if (currentUri != null && viewMode) {
            getActivity().setTitle(getString(R.string.detail_book_title));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EXISTING_ITEM_LOADER, null, this);
        } else if (currentUri != null && !viewMode) {
            getActivity().setTitle(getString(R.string.edit_book_title));
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        ((MainActivity) getActivity()).setOnBackPressedListener(new OnBackPressedListener() {
            @Override
            public void doBack() {
                viewInventory();
            }
        });

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!viewMode || currentUri == null) {
            inflater.inflate(R.menu.menu_edit, menu);
        } else
            inflater.inflate(R.menu.menu_view, menu);
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        ((MainActivity) getContext()).menuItemsColor(menu, Color.WHITE);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_GENRE,
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

        if (cursor == null || cursor.getCount() < 1) return;

        if (cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int genreIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_GENRE);
            int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);


            String name = cursor.getString(nameIndex);
            String genre = cursor.getString(genreIndex);
            Double price = cursor.getDouble(priceIndex);
            Integer quantity = cursor.getInt(quantityIndex);
            String supplierName = cursor.getString(supplierNameIndex);
            String supplierPhone = cursor.getString(supplierPhoneIndex);

            nameEditText.setText(name);
            genreEditText.setText(genre);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        nameEditText.setText(null);
        genreEditText.setText(null);
        priceEditText.setText(null);
        quantityEditText.setText(null);
        supplierNameEditText.setText(null);
        supplierPhoneEditText.setText(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.action_edit:
                bundle.putString("viewMode", "0");
                EditorFragment fragment = new EditorFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .commit();
                return true;
            case R.id.action_save:
                saveItem();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_inventory:
                this.viewInventory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final void viewInventory() {
        if (mItemHasChanged) showUnsavedChangesDialog(discardButon);
        else {
            getActivity().getIntent().setData(null);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new InventoryFragment())
                    .commit();
        }
    }

    private void saveItem() {

        String nameString = nameEditText.getText().toString().trim();
        String genreString = genreEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();


        /**
         *  validation fields
         */
        if (viewMode == false) {

            String attribute = null;
            if (TextUtils.isEmpty(nameString)) {
                attribute = "Name";
            } else if (TextUtils.isEmpty(priceString)) {
                attribute = "Price";
            } else if (TextUtils.isEmpty(quantityString)) {
                attribute = "Quantity";
            }
            if (attribute != null) {
                Toast.makeText(getContext(), getContext().getString(R.string.valid_attribute_message).replace("{attr}", attribute), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_GENRE, genreString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Double.valueOf(priceString));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.valueOf(quantityString));
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        if (currentUri == null) {
            Uri newUri = getContext().getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            currentUri = newUri;
            if (newUri == null) {
                Toast.makeText(getContext(), R.string.error_saving_toasty, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.saved_book_toasty, Toast.LENGTH_SHORT).show();
            }

        } else {
            int updatedRows = getContext().getContentResolver().update(currentUri, values, null, null);

            if (updatedRows == 0) {
                Toast.makeText(getContext(), R.string.error_updating_toasty, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.book_updated_toasty, Toast.LENGTH_SHORT).show();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("viewMode", "1");
        getActivity().getIntent().setData(currentUri);
        getFragmentManager().beginTransaction().
                replace(R.id.frame_layout, new EditorFragment()).commit();
    }

    private void deleteItem() {

        if (currentUri == null) return;
        int deletedRows = getActivity().getContentResolver().delete(currentUri, null, null);
        if (deletedRows == -1) {
            Toast.makeText(getContext(), R.string.book_delete_failed_toasty, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.book_deleted_successfully, Toast.LENGTH_SHORT).show();
        }

    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.discard_message_dialog);
        builder.setPositiveButton(R.string.discard_action, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_edit_action, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(R.string.delete_book__message_dialog);
        builder.setPositiveButton(R.string.delete_action, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
                getActivity().getIntent().setData(null);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new InventoryFragment())
                        .commit();
            }
        });
        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) dialog.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
