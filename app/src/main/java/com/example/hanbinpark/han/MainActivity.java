package com.example.hanbinpark.han;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.hanbinpark.han.Login.LoginActivity;
import com.example.hanbinpark.han.Login.RegisterActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        //Check user already login or not
        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0) {
            checkCurrentUser();
        }else {
            SaveSharedPreference.clearUserName(MainActivity.this);
        }
    }

    private void checkCurrentUser(){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
