package com.example.codebrains;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class JobPagerAdapter extends FragmentStateAdapter {
    private final List<JobFragment> fragments = new ArrayList<>();
    private final JobAdapter.OnJobCloseListener closeListener;

    public JobPagerAdapter(@NonNull FragmentActivity fragmentActivity, JobAdapter.OnJobCloseListener listener) {
        super(fragmentActivity);
        this.closeListener = listener;
        createFragments();
    }

    private void createFragments() {
        fragments.add(JobFragment.newInstance("All", closeListener));
        fragments.add(JobFragment.newInstance("Open", closeListener));
        fragments.add(JobFragment.newInstance("In Progress", closeListener));
        fragments.add(JobFragment.newInstance("Completed", closeListener));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public List<JobFragment> getFragments() {
        return fragments;
    }
}