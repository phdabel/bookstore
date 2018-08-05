package abelcorrea.com.br.bookstore.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.adapters.ProductCursorAdapter;
import abelcorrea.com.br.bookstore.data.BookStoreContract.ProductEntry;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductCursorAdapter adapter;

    private static final int PRODUCT_LOADER = 0;

    @BindView(R.id.list) ListView products;
    @BindView(R.id.add_fab) FloatingActionButton add_btn;
    @BindView(R.id.empty_view) View emptyView;

    public InventoryFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this,view);
        products.setEmptyView(emptyView);

        adapter = new ProductCursorAdapter(getContext(),null);
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

        add_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

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
}


