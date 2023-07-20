package com.example.mybirdy;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GameView extends View {
    private Object object;
    private Handler handler;
    private Runnable runnable;
    private ArrayList<Obstacle> arrObs;
    private int sumObs;
    private int distanceObs;
    private int score, bestScore = 0;
    private boolean start;
    private Context context;
    private DocumentReference userRef;
    private SharedPreferences sharedPreferences;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        bestScore = sharedPreferences.getInt("HIGH_SCORE", 0);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userRef = db.collection("USERS").document(userId);

            // Listen for authentication state changes
            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in, retrieve the high score
                        retrieveHighScore();
                    }
                }
            });
        }

        score = 0;
        start = false;
        initObject();
        initObstacle();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
    }

    private void initObstacle() {
        sumObs = 4;
        // Distance
        distanceObs = 400 * Constants.SCREEN_HEIGHT / 1920;
        arrObs = new ArrayList<>();
        for (int i = 0; i < sumObs; i++) {
            if (i < sumObs / 2) {
                Obstacle obstacle = new Obstacle(Constants.SCREEN_WIDTH + i * ((Constants.SCREEN_WIDTH + 200 * Constants.SCREEN_WIDTH / 1080) / (sumObs / 2)),
                        0, 200 * Constants.SCREEN_WIDTH / 1080, Constants.SCREEN_HEIGHT / 2);
                obstacle.setBm(BitmapFactory.decodeResource(getResources(), R.drawable.stick1));
                obstacle.randomN();
                arrObs.add(obstacle);
            } else {
                Obstacle obstacle = new Obstacle(arrObs.get(i - sumObs / 2).getX(), arrObs.get(i - sumObs / 2).getY()
                        + arrObs.get(i - sumObs / 2).getHeight() + distanceObs, 200 * Constants.SCREEN_WIDTH / 1080, Constants.SCREEN_HEIGHT / 2);
                obstacle.setBm(BitmapFactory.decodeResource(getResources(), R.drawable.stick2));
                arrObs.add(obstacle);
            }
        }
    }

    private void initObject() {
        object = new Object();
        object.setWidth(100 * Constants.SCREEN_WIDTH / 1080);
        object.setHeight(100 * Constants.SCREEN_HEIGHT / 1920);
        object.setX(100 * Constants.SCREEN_WIDTH / 1080);
        object.setY(Constants.SCREEN_HEIGHT / 2 - object.getHeight() / 2);
        ArrayList<Bitmap> arrBms = new ArrayList<>();
        arrBms.add(BitmapFactory.decodeResource(getResources(), R.drawable.pinky1));
        arrBms.add(BitmapFactory.decodeResource(getResources(), R.drawable.pinky2));
        object.setArrBms(arrBms);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (start) {
            object.draw(canvas);
            for (int i = 0; i < sumObs; i++) {
                if (object.getRect().intersect(arrObs.get(i).getRect()) || object.getY() - object.getHeight() < 0 || object.getY() > Constants.SCREEN_HEIGHT) {
                    Obstacle.speed = 0;
                    MainActivity.txt_score_over.setText(MainActivity.txt_score.getText());
                    MainActivity.txt_best_score.setText("Best:" + bestScore);
                    MainActivity.txt_score.setVisibility(INVISIBLE);
                    MainActivity.rl_game_over.setVisibility(VISIBLE);

                    saveHighScoreToFirestore();

                }
                if (this.object.getX() + this.object.getWidth() > arrObs.get(i).getX() + arrObs.get(i).getWidth() / 2
                        && this.object.getX() + this.object.getWidth() <= arrObs.get(i).getX() + arrObs.get(i).getWidth() / 2 + Obstacle.speed
                        && i < sumObs / 2) {
                    score++;
                    if (score > bestScore) {
                        bestScore = score;
                        saveHighScoreToFirestore();
                    }
                    MainActivity.txt_score.setText(String.valueOf(score));
                }

                if (this.arrObs.get(i).getX() < -arrObs.get(i).getWidth()) {
                    this.arrObs.get(i).setX(Constants.SCREEN_WIDTH);
                    if (i < sumObs / 2) {
                        arrObs.get(i).randomN();
                    } else {
                        arrObs.get(i).setY(this.arrObs.get(i - sumObs / 2).getY()
                                + this.arrObs.get(i - sumObs / 2).getHeight() + this.distanceObs);
                    }
                }
                this.arrObs.get(i).draw(canvas);
            }
        } else {
            if (object.getY() > Constants.SCREEN_HEIGHT / 2) {
                object.setDrop(-15 * Constants.SCREEN_HEIGHT / 1920);
            }
            object.draw(canvas);
        }

        handler.postDelayed(runnable, 10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            object.setDrop(-15);
        }
        return true;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void reset() {
        MainActivity.txt_score.setText("0");
        score = 0;
        initObject();
        initObstacle();
    }

    private void retrieveHighScore() {
        if (userRef != null) {
            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Integer highScore = documentSnapshot.getLong("HIGH_SCORE").intValue();
                                if (highScore != null) {
                                    bestScore = highScore;
                                    // Update locally stored high score
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("HIGH_SCORE", bestScore);
                                    editor.apply();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to retrieve high score from Firestore
                        }
                    });
        }
    }

    private void saveHighScoreToFirestore() {
        if (userRef != null) {
            userRef.update("HIGH_SCORE", bestScore)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // High score successfully updated in Firestore
                            // Also, save the high score locally
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("HIGH_SCORE", bestScore);
                            editor.apply();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update high score in Firestore
                        }
                    });
        }
    }
}
