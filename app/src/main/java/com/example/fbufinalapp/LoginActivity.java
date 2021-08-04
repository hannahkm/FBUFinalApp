package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivityLoginBinding;
import com.parse.ParseUser;

/**
 * Activity for the user to log into their account. Allows them to also register for an account.
 */
public class LoginActivity extends AppCompatActivity {
    TextView tvUsername;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        getSupportActionBar().setTitle("Login");

        tvUsername = findViewById(R.id.tvUsername);
        tvPassword = findViewById(R.id.tvPassword);

        binding.btLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        binding.btSignUp.setOnClickListener(new View.OnClickListener() {
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
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                startActivity(i);
            } else {
                Log.e("Login Failed", String.valueOf(e));
                Toast.makeText(LoginActivity.this, "Couldn't log you in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}