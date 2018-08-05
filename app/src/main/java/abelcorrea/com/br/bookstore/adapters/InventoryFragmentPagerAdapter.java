package abelcorrea.com.br.bookstore.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import abelcorrea.com.br.bookstore.R;
import abelcorrea.com.br.bookstore.fragments.InventoryFragment;

public class InventoryFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public InventoryFragmentPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new InventoryFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return mContext.getString(R.string.inventory_fragment);
        }
        return null;
    }
}
