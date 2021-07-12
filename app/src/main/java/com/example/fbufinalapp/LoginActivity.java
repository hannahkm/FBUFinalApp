package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    TextView tvUsername;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // send user straight to main activity
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        }

        Button loggingIn = findViewById(R.id.btLogIn);
        TextView signup = findViewById(R.id.btSignUp);

        loggingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    public void logIn(){
        String username = String.valueOf(tvUsername.getText());
        String password = String.valueOf(tvPassword.getText());

        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (user != null) {
                // Hooray! The user is logged in.
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                startActivity(i);
            } else {
                // Login failed. Look at the ParseException to see what happened.
                Log.e("Login Failed", String.valueOf(e));
                Toast.makeText(LoginActivity.this, "Couldn't log you in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}