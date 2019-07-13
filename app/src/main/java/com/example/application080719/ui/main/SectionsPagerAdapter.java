package com.example.application080719.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.application080719.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.sounds, R.string.breath, R.string.brief_practices, R.string.body_scan, R.string.meditations};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Bundle bundle = new Bundle();
        if (position == 0) {

        } else if (position == 1) {
            bundle.putInt(null, position);
        } else if (position == 2) {
            bundle.putInt(null, position);
        } else if (position == 3) {
            bundle.putInt(null, position);
        } else {
            bundle.putInt(null, position);
        }
        CommonFragment fragment = new CommonFragment();
        fragment.setArguments(bundle);
        return fragment;
//            return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 5 total pages.
        return 5;
    }
}