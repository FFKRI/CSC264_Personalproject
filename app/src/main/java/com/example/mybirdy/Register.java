package com.example.mybirdy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword, editTextName;
    private FirebaseAuth mAuth;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);
        buttonRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.loginNow);

        TextView loginNowTextView = findViewById(R.id.loginNow);
        String text = loginNowTextView.getText().toString();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFFFF")), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        loginNowTextView.setText(spannableStringBuilder);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToLogin();
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Register.this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Register.this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(Register.this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            DbQuery dbQuery = new DbQuery(firestore);

                            dbQuery.createUserData(email, name, new DbQuery.MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    startActivity(new Intent(Register.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(Register.this, "Something went wrong! Please Try Again Later", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onUserDataRetrieved(DbQuery.RankModel userModel) {
                                    // Handle user data retrieval
                                }


                            });
                        } else {
                            Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }
}
