package com.example.codebrains;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final String jobId;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String jobId) {
        super(fragmentActivity);
        this.jobId = jobId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return getFragmentForPosition(position);
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }

    private Fragment getFragmentForPosition(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new DetailsFragment();
                break;
            case 1:
                fragment = new ProposalsFragment();
                break;
            case 2:
                fragment = new FilesFragment();
                break;
            default:
                throw new IllegalArgumentException("Invalid tab position: " + position);
        }

        // Pass jobId to the fragment via Bundle
        fragment.setArguments(createBundleWithJobId());
        return fragment;
    }

    private Bundle createBundleWithJobId() {
        Bundle bundle = new Bundle();
        bundle.putString("jobId", jobId);
        return bundle;
    }
}
