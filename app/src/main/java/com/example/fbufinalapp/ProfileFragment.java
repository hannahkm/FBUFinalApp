package com.example.fbufinalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbufinalapp.databinding.FragmentProfileBinding;
import com.parse.ParseUser;

/**
 * Fragment subclass for the user's profile page. Allows the user to log out or sign up
 * for an account
 */
public class ProfileFragment extends Fragment {
    Context context;
    FragmentProfileBinding binding;
    ParseUser currentUser;
    public static final String TAG = "ProfileFragment";
    boolean newUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle("Profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        currentUser = ParseUser.getCurrentUser();
        newUser = (currentUser.getEmail() == null);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (newUser){
            binding.btLogOut.setText("Sign Up");
        }

        binding.btLogOut.setOnClickListener(v -> {
            if (newUser) {
                Intent i = new Intent(context, SignUpActivity.class);
                startActivity(i);
            } else {
                logUserOut logOut = new logUserOut();
                logOut.start();
                try {
                    logOut.join();
                } catch (Exception e){
                    Log.e(TAG, String.valueOf(e));
                }
            }
        });

        binding.tvUsername.setText(currentUser.getUsername());

    }

    class logUserOut extends Thread{
        @Override
        public void run() {
            ParseUser.logOut();
            Intent i = new Intent(context, LoginActivity.class);
            startActivity(i);
        }
    }

}