package com.example.mybirdy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button button;
    private TextView textView;
    private FirebaseUser user;
    private DbQuery dbQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        dbQuery = new DbQuery(FirebaseFirestore.getInstance());
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = mAuth.getCurrentUser();

        if (user == null) {
            button.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
            button.setVisibility(View.VISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        TextView registerNowTextView = findViewById(R.id.registerNow);
        String text = registerNowTextView.getText().toString();

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#800080")), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        registerNowTextView.setText(spannableStringBuilder);

    }
}