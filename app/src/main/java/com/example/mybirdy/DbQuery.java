package com.example.mybirdy;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbQuery {
    public FirebaseFirestore g_firestore;

    List<RankModel> g_userList = new ArrayList<>();
    private boolean isMenOnTopList;

    public DbQuery(FirebaseFirestore firestore) {
        this.g_firestore = firestore;
    }

    public void createUserData(String email, String name, MyCompleteListener completeListener) {
        Map<String, java.lang.Object> userData = new HashMap<>();

        userData.put("EMAIL_ID", (java.lang.Object) email);
        userData.put("NAME", (java.lang.Object) name);
        userData.put("HIGH_SCORE", (java.lang.Object) 0);
        userData.put("lastUpdated", FieldValue.serverTimestamp()); // Set the lastUpdated field with a Timestamp

        DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        WriteBatch batch = g_firestore.batch();

        batch.set(userDoc, userData);

        DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
        batch.update(countDoc, "COUNT", FieldValue.increment(1));

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }


    public void updateUserName(String userId, String newName, MyCompleteListener completeListener) {
        DocumentReference userDoc = g_firestore.collection("USERS").document(userId);

        Map<String,java.lang.Object > updates = new HashMap<>();
        updates.put("NAME", newName);

        userDoc.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }


    public void getTopUsersList(MyCompleteListener completeListener) {
        g_firestore.collection("USERS")
                .orderBy("HIGH_SCORE", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        g_userList.clear();

                        int rank = 1;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String name = documentSnapshot.getString("NAME");
                            int highScore = documentSnapshot.getLong("HIGH_SCORE").intValue();

                            RankModel rankModel = new RankModel(rank, name, highScore);
                            g_userList.add(rankModel);

                            rank++;
                        }

                        completeListener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }


    public void getUserData(MyCompleteListener completeListener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDoc = g_firestore.collection("USERS").document(userId);

        userDoc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("NAME");
                            int highScore = documentSnapshot.getLong("HIGH_SCORE").intValue();
                            Timestamp lastUpdatedTimestamp = documentSnapshot.getTimestamp("lastUpdated"); // Retrieve lastUpdated as a Timestamp
                            RankModel userModel = new RankModel(-1, name, highScore);
                            completeListener.onUserDataRetrieved(userModel);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        completeListener.onFailure();
                    }
                });
    }

    public boolean isMenOnTopList(int myHighScore) {
        if (g_userList.isEmpty()) {
            return false;
        }

        int topScore = g_userList.get(0).getHighScore();
        return myHighScore >= topScore;
    }

    public List<RankModel> getUserList() {
        return g_userList;
    }

    public List<RankModel> getTopUserList() {
        return g_userList;
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


    public interface MyCompleteListener {
        void onSuccess();

        void onFailure();

        void onUserDataRetrieved(RankModel userModel);

    }
}