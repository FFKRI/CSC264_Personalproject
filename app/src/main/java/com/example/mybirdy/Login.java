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

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    FirebaseAuth mAuth;
    Button buttonLog,log_out;
    ProgressBar progressBar;
    TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLog = findViewById(R.id.btn_sign_in);
        progressBar = findViewById(R.id.progressBar);
        textViewRegister = findViewById(R.id.registerNow);


        TextView registerNowTextView = findViewById(R.id.registerNow);
        String text = registerNowTextView.getText().toString();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFFFF")), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerNowTextView.setText(spannableStringBuilder);

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

