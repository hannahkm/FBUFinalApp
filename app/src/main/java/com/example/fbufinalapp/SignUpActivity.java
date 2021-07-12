package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    TextView tvUsername;
    TextView tvPassword;
    TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        tvEmail = findViewById(R.id.tvEmail);

        Button signup = findViewById(R.id.btSignUp);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });
    }

    public void SignUp(){
        String username = String.valueOf(tvUsername.getText());
        String password = String.valueOf(tvPassword.getText());
        String email = String.valueOf(tvEmail.getText());

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(e -> {
            if (e == null) {
                // Hooray! Let them use the app now.
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                Toast.makeText(SignUpActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                startActivity(i);
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                Log.i("SignUp Failed", String.valueOf(e));
                Toast.makeText(SignUpActivity.this, "Couldn't sign you up", Toast.LENGTH_SHORT).show();
            }
        });
    }
}