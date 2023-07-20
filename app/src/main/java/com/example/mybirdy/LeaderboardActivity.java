package com.example.mybirdy;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private ListView listView;
    private RankAdapter rankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.listView);

        // Create an instance of DbQuery
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DbQuery dbQuery = new DbQuery(firestore);

        // Create the adapter
        rankAdapter = new RankAdapter(this, new ArrayList<>());
        listView.setAdapter(rankAdapter);

        // Retrieve the top user list from DbQuery
        dbQuery.getTopUsersList(new DbQuery.MyCompleteListener() {
            @Override
            public void onSuccess() {
                List<DbQuery.RankModel> rankList = dbQuery.getUserList();

                // Convert DbQuery.RankModel to RankAdapter.RankModel
                List<RankAdapter.RankModel> adapterRankList = new ArrayList<>();
                for (DbQuery.RankModel rankModel : rankList) {
                    RankAdapter.RankModel adapterRankModel = new RankAdapter.RankModel(rankModel.getRank(), rankModel.getName(), rankModel.getHighScore());
                    adapterRankList.add(adapterRankModel);
                }

                rankAdapter.updateData(adapterRankList);
            }

            @Override
            public void onFailure() {
                // Handle failure case
            }

            @Override
            public void onUserDataRetrieved(DbQuery.RankModel userModel) {
                // Not used in this context
            }
        });
    }
}
