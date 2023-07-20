package com.example.mybirdy;

public interface MyCompleteListener {
    void onSuccess();
    void onFailure();
    void onUserDataRetrieved(DbQuery.RankModel userModel);
}


