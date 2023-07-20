package com.example.mybirdy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RankAdapter extends ArrayAdapter<RankAdapter.RankModel> {

    private Context mContext;
    private List<RankModel> mRankList;

    public RankAdapter(Context context, List<RankModel> rankList) {
        super(context, 0, rankList);
        mContext = context;
        mRankList = rankList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.rank_item_layout, parent, false);
        }

        // Get the current RankModel
        RankModel currentRank = mRankList.get(position);

        // Set the rank number
        TextView rankTextView = itemView.findViewById(R.id.rank);
        rankTextView.setText("Rank - " + currentRank.getRank());

        // Set the name
        TextView nameTextView = itemView.findViewById(R.id.name);
        nameTextView.setText(currentRank.getName());

        // Set the score
        TextView scoreTextView = itemView.findViewById(R.id.score);
        scoreTextView.setText("Score: " + currentRank.getHighScore());

        // Set the image text (you can modify this based on your requirements)
        TextView imgTextView = itemView.findViewById(R.id.img_text);
        imgTextView.setText(currentRank.getName().substring(0, 1));

        return itemView;
    }

    public void updateData(List<RankModel> rankList) {
        mRankList.clear();
        mRankList.addAll(rankList);
        notifyDataSetChanged();
    }

    public static class RankModel {
        private int rank;
        private String name;
        private int highScore;

        public RankModel(int rank, String name, int highScore) {
            this.rank = rank;
            this.name = name;
            this.highScore = highScore;
        }

        public int getRank() {
            return rank;
        }

        public String getName() {
            return name;
        }

        public int getHighScore() {
            return highScore;
        }
    }
}
