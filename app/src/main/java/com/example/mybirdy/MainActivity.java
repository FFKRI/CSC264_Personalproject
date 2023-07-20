package com.example.mybirdy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MainActivity extends AppCompatActivity {

    public static TextView txt_score, txt_best_score, txt_score_over;
    public static RelativeLayout rl_game_over;
    public static Button btn_about_me, btn_start, btn_high_score, btn_sign_in, btn_change_name, btn_delete_account;
    public GameView gv;
    public ImageView img_logo;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the status Bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide the navigation bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_HEIGHT = dm.heightPixels;
        Constants.SCREEN_WIDTH = dm.widthPixels;
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        txt_score = findViewById(R.id.text_score);
        txt_best_score = findViewById(R.id.txt_best_score);
        txt_score_over = findViewById(R.id.txt_score_over);
        rl_game_over = findViewById(R.id.rl_game_over);
        btn_start = findViewById(R.id.btn_start);
        btn_high_score = findViewById(R.id.btn_high_score);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_change_name = findViewById(R.id.btn_change_name);
        btn_delete_account = findViewById(R.id.btn_delete_account);
        btn_about_me = findViewById(R.id.btn_about_me);
        gv = findViewById(R.id.gv);
        img_logo = findViewById(R.id.img_logo);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setStart(true);
                txt_score.setVisibility(View.VISIBLE);
                btn_start.setVisibility(View.INVISIBLE);
                btn_high_score.setVisibility(View.INVISIBLE);
                btn_sign_in.setVisibility(View.INVISIBLE);
                btn_change_name.setVisibility(View.INVISIBLE);
                btn_delete_account.setVisibility(View.INVISIBLE);
                btn_about_me.setVisibility(View.INVISIBLE);
                img_logo.setVisibility(View.INVISIBLE);
                btn_change_name.setVisibility(View.GONE);
                btn_delete_account.setVisibility(View.GONE);
            }
        });

        btn_high_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });

        // Add click listener for the "Sign In" button
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                // Handle the button click event for sign in
            }
        });

        // Check if the user is logged in
        if (user != null) {
            btn_change_name.setVisibility(View.VISIBLE);
            btn_delete_account.setVisibility(View.VISIBLE);
        } else {
            btn_change_name.setVisibility(View.INVISIBLE);
            btn_delete_account.setVisibility(View.INVISIBLE);
        }

        // Add click listener for the "Change Name" button
        btn_change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    showNameInputDialog();
                }
            }
        });

        // Add click listener for the "Delete Account" button
        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAccountDialog();
            }
        });

        rl_game_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_start.setVisibility(View.VISIBLE);
                btn_sign_in.setVisibility(View.VISIBLE);
                btn_high_score.setVisibility(View.VISIBLE);
                btn_about_me.setVisibility(View.VISIBLE);
                img_logo.setVisibility(View.VISIBLE);
                rl_game_over.setVisibility(View.INVISIBLE);
                gv.setStart(false);
                gv.reset();

                // Check if the user is logged in and hide the "Change Name" and "Delete Account" buttons accordingly
                if (user != null) {
                    btn_change_name.setVisibility(View.VISIBLE);
                    btn_delete_account.setVisibility(View.VISIBLE);
                } else {
                    btn_change_name.setVisibility(View.INVISIBLE);
                    btn_delete_account.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_about_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutDeveloperActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if the user is logged in
        user = mAuth.getCurrentUser();
        if (user != null) {
            btn_change_name.setVisibility(View.VISIBLE);
            btn_delete_account.setVisibility(View.VISIBLE);
        } else {
            btn_change_name.setVisibility(View.INVISIBLE);
            btn_delete_account.setVisibility(View.INVISIBLE);
        }
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change Name");

        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    updateUserName(newName);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateUserName(String newName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DbQuery dbQuery = new DbQuery(firestore);

        dbQuery.updateUserName(user.getUid(), newName, new DbQuery.MyCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserDataRetrieved(DbQuery.RankModel userModel) {
                // Not used in this context
            }


        });
    }

    private void showDeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccountData(user.getUid());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteAccountData(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Delete user document from "USERS" collection
        firestore.collection("USERS")
                .document(userId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User document deleted successfully
                        deleteAdditionalData(userId); // Call the method to delete additional data after the user document deletion
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete user document
                    }
                });
    }

    private void deleteAdditionalData(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Delete the user's high score document
        firestore.collection("USERS")
                .document(userId)
                .collection("HIGH_SCORE")
                .document("SCORE")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // High score document deleted successfully
                        deleteNameData(userId); // Call the method to delete the user's name document after the high score document deletion
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete high score document
                    }
                });
    }



    private void deleteNameData(String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Delete the user's name document
        firestore.collection("USERS")
                .document(userId)
                .collection("NAME")
                .document("USERNAME")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Name document deleted successfully
                        // At this point, all user data has been deleted
                        deleteAccount(); // Call the method to delete the user's authentication account
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete name document
                    }
                });
    }

    private void deleteAccount() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUser.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Account deleted successfully
                            Toast.makeText(MainActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            btn_delete_account.setVisibility(View.GONE);
                            btn_change_name.setVisibility(View.GONE);
                            btn_sign_in.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete account
                            Toast.makeText(MainActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
