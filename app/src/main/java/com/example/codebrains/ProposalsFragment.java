package com.example.codebrains;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProposalsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposals, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ProposalsAdapter(getSampleData()));

        return view;
    }

    private List<Proposal> getSampleData() {
        List<Proposal> list = new ArrayList<>();
        list.add(new Proposal("sam", "23212424", 4.9f, "I have extensive experience in web development...", "$500.00"));
        list.add(new Proposal("rushi", "23212424", 4.9f, "I'm a professional developer...", "$200.00"));
        return list;
    }
}