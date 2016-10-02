package com.james.bagels.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.james.bagels.data.Bagel;
import com.james.bagels.fragments.BagelFragment;

import java.util.List;

public class BagelAdapter extends FragmentStatePagerAdapter {

    public List<Bagel> bagels;

    public BagelAdapter(FragmentManager fm, List<Bagel> bagels) {
        super(fm);
        this.bagels = bagels;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putParcelable(BagelFragment.EXTRA_BAGEL, bagels.get(position));

        Fragment fragment = new BagelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return bagels.size();
    }
}
