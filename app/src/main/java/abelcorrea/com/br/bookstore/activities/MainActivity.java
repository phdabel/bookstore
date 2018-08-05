package abelcorrea.com.br.bookstore.activities;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.fragments.InventoryFragment;
import abelcorrea.com.br.bookstore.fragments.OnBackPressedListener;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    protected OnBackPressedListener onBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // pass extras of the intent
        InventoryFragment inventory = new InventoryFragment();
        inventory.setArguments(getIntent().getExtras());
        // add the fragment to the frame layout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, inventory).commit();

    }

    @Override
    public void onBackPressed(){
        if(onBackPressedListener != null) {
            onBackPressedListener.doBack();
            onBackPressedListener = null;
        }else
            super.onBackPressed();
    }

}