package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.View;
import android.widget.Toast;

import com.example.fbufinalapp.adapters.UserAdapter;
import com.example.fbufinalapp.databinding.ActivityUserSearchBinding;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity {
    ActivityUserSearchBinding binding;
    List<ParseUser> users;
    UserAdapter adapter;
    static int USER_SHARED_SUCCESS = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserSearchBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        getWindow().setEnterTransition(new Slide());
        getSupportActionBar().setTitle("Search for a user");

        users = new ArrayList<>();
        adapter = new UserAdapter(this, users);

        binding.rvResults.setAdapter(adapter);
        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query.whereStartsWith(CommonValues.KEY_USERNAME, String.valueOf(s));
                query.findInBackground((objects, e) -> {
                    if(e == null){
                        users.clear();
                        users.addAll(objects);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(UserSearchActivity.this, "User couldn't be found", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void userClicked(ParseUser user){
        Intent i = new Intent();
        i.putExtra("userId", user.getObjectId());
        setResult(USER_SHARED_SUCCESS, i);
        finish();
    }
}