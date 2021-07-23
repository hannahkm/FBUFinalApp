package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivitySignUpBinding;
import com.parse.ParseUser;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    TextView tvUsername;
    TextView tvPassword;
    TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Register");

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);
        tvEmail = findViewById(R.id.tvEmail);

        binding.btSignUp.setOnClickListener(new View.OnClickListener() {
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
        user.put("favorites", new ArrayList<>());
        user.put("itineraries", new ArrayList<>());

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