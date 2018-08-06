package abelcorrea.com.br.bookstore.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import abelcorrea.com.br.bookstore.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyViewFragment extends Fragment {


    public EmptyViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_view, container, false);
        return view;
    }

}
