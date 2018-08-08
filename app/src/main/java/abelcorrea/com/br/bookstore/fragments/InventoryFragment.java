package abelcorrea.com.br.bookstore.fragments;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.activities.MainActivity;
import abelcorrea.com.br.bookstore.activities.SettingsActivity;
import abelcorrea.com.br.bookstore.adapters.BookCursorAdapter;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private BookCursorAdapter adapter;

    private static final int PRODUCT_LOADER = 0;

    @BindView(R.id.list)
    ListView products;
    @BindView(R.id.add_fab)
    FloatingActionButton add_btn;
    @BindView(R.id.empty_view) View emptyView;

    public InventoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this, view);
        products.setEmptyView(emptyView);
        getActivity().setTitle(getString(R.string.inventory_fragment));
        adapter = new BookCursorAdapter(getContext(), null);
        products.setAdapter(adapter);

        products.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                getActivity().getIntent().setData(currentUri);

                EditorFragment editor = new EditorFragment();

                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, editor).commit();

            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // pass extras of the intent
                EditorFragment editor = new EditorFragment();
                getActivity().getIntent().setData(null);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, editor).commit();

            }
        });

        LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(PRODUCT_LOADER, null, this);
        return view;

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        return new CursorLoader(getContext(),
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inventory, menu);
        ((MainActivity)getActivity()).menuItemsColor(menu, Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(R.string.delete_all_entries_dialog);
        builder.setPositiveButton(R.string.delete_action, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteAllItems();
                getLoaderManager().restartLoader(PRODUCT_LOADER, null, InventoryFragment.this);
            }
        });
        builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null) dialog.dismiss();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public int deleteAllItems(){
        int deletedRows = getContext().getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if(deletedRows == -1){
            Toast.makeText(getContext(), getString(R.string.book_delete_failed_toasty), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), String.valueOf(deletedRows) + getString(R.string.mass_delete_success), Toast.LENGTH_SHORT).show();
        }
        return deletedRows;
    }

}


