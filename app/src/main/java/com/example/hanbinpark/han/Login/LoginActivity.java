package com.example.hanbinpark.han.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hanbinpark.han.MainActivity;
import com.example.hanbinpark.han.R;
import com.example.hanbinpark.han.SaveSharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends LoginSetting {
    private static final String TAG = "LoginActivity";

    static final int REQUEST_EXIT = 1;

    private Context mContext;
    private String email, password;
    private EditText mEmail, mPassword;
    private Button btnRegister, btnLogin, btnForgotPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");

        //Set up widgets
        mContext = LoginActivity.this;
        mEmail = findViewById(R.id.input_email_login);
        mPassword = findViewById(R.id.input_password_login);
        btnLogin = findViewById(R.id.btn_login_login);
        btnRegister = findViewById(R.id.btn_signup_login);
        btnForgotPass = findViewById(R.id.btn_forgotPass_login);

        //Set up register button. Change text color when button clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_EXIT);
                changeHintColorToGrey();
            }
        });

        //Set up forgot password button. Change text color when button clicked
        btnForgotPass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ResetPasswordActivity.class);
                startActivity(intent);
                changeHintColorToGrey();
            }
        });



        setLoginBtn();
        //Set up EditText hint color to red when button clicked
        setEditText(mEmail);
        setEditText(mPassword);

        //Hide keyboard and focus, when tap outside of editText
        findViewById(R.id.relativeLayout_login).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(getCurrentFocus() != null){
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
                return true;
            }
        });

    }


    //get result from other intent and finish this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                this.finish();

            }
        }
    }

    //Set up login button
    private void setLoginBtn(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get email and password text from user
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();

                //Check all input are filled
                if(email.equals("") || password.equals("")){
                    mEmail.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mPassword.setHintTextColor(getResources().getColor(R.color.lightRed));
                }else{

                    //Check email format
                    if(!LoginSetting.emailvalidation(email)){
                        Toast.makeText(getApplicationContext(), "Email is not Correct Format", Toast.LENGTH_LONG).show();
                    }else {

                        try {

                            String url = "http://10.1.237.23/Han/Han/login.php";


                            // Communicated with php code by http post
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);

                                                if (jsonObject.getString("status").equals("200")) {
                                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                                    //store data to login automatically
                                                    SaveSharedPreference.setUserName(LoginActivity.this, jsonObject.getString("email"));
                                                    finish();
                                                }
                                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("password", password);
                                    params.put("email", email);
                                    return params;
                                }
                            };

                            //Send the request
                            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                            requestQueue.add(stringRequest);


                        } catch (Exception e) {
                            new String("Exception: " + e.getMessage());
                            Log.d(TAG, new String("Exception: " + e.getMessage()));

                        }
                    }

                }
            }
        });
    }

    //Set up change editText hint color to grey
    private void changeHintColorToGrey(){
        mEmail.setHintTextColor(getResources().getColor(R.color.grey));
        mPassword.setHintTextColor(getResources().getColor(R.color.grey));

    }



}
