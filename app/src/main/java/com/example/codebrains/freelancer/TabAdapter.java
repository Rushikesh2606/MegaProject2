package com.example.codebrains.freelancer;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.codebrains.R;

public class TabAdapter extends FragmentPagerAdapter {
    private final Context context;

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return CategoryFragment.newInstance(
                context.getResources().getStringArray(R.array.job_categories)[position]
        );
    }

    @Override
    public int getCount() {
        return context.getResources().getStringArray(R.array.job_categories).length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.job_categories)[position];
    }
}