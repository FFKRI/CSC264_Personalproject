package com.example.mybirdy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybirdy.DbQuery;
import com.example.mybirdy.R;
import com.example.mybirdy.RankAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoardFragment extends Fragment {
    private ListView listView;
    private RankAdapter adapter;
    private DbQuery dbQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_leaderboard, container, false);
        listView = view.findViewById(R.id.listView);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        dbQuery = new DbQuery(firestore);

        adapter = new RankAdapter(getContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        return view;
    }
}



