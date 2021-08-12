package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivitySignUpBinding;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Activity for the user to sign up for an account
 */
public class SignUpActivity extends AppCompatActivity {
    ParseUser currentUser;
    TextView tvUsername;
    TextView tvPassword;
    TextView tvEmail;
    boolean newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        currentUser = ParseUser.getCurrentUser();

        getSupportActionBar().setTitle("Register");

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        tvEmail = findViewById(R.id.tvEmail);

        newUser = currentUser == null;

        binding.btSignUp.setOnClickListener(v -> SignUp());

        binding.btLogin.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(i);
        });
    }

    /**
     * Either creates a new account or updates the current guest account with the user's information
     */
    public void SignUp(){
        String username = String.valueOf(tvUsername.getText());
        String password = String.valueOf(tvPassword.getText());
        String email = String.valueOf(tvEmail.getText());

        ParseUser user = newUser ? new ParseUser() : currentUser;

        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        if (newUser){
            user.put(CommonValues.KEY_FAVORITES, new ArrayList<>());
            user.put(CommonValues.KEY_ITINERARY_USER, new ArrayList<>());

            user.signUpInBackground(e -> {
                userSave(e);
            });
        } else {
            user.saveInBackground(e -> {
                userSave(e);
            });
        }

    }

    public void userSave(ParseException e) {
        if (e == null) {
            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
            Toast.makeText(SignUpActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            startActivity(i);
        } else {
            Toast.makeText(SignUpActivity.this, "Couldn't sign you up", Toast.LENGTH_SHORT).show();
        }
    }
}