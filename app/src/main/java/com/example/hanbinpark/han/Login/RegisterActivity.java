package com.example.hanbinpark.han.Login;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.hanbinpark.han.Login.LoginActivity.REQUEST_EXIT;

public class RegisterActivity extends LoginSetting {
    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String username, password, repeatPassword, email, firstName, lastName, fullName;
    private EditText mUsername, mPassword, mRepeatPassword, mEmail, mFirstName, mLastName;
    private Button signUpBtn, cancelBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.d(TAG, "onCreate: started");


        //initialize widget
        initWidget();

        //activate Sign up Button
        setSignUpBtn();

        //set click listener to cancel btn.
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Hide keyboard and focus, when tap outside of editText
        findViewById(R.id.linearLayout_signup).setOnTouchListener(new View.OnTouchListener() {
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

    //set Sign Up button
    private void setSignUpBtn() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHintColorToGrey();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                repeatPassword = mRepeatPassword.getText().toString();
                email = mEmail.getText().toString();
                firstName = mFirstName.getText().toString();
                lastName = mLastName.getText().toString();

                //if EditText is null change hint color as red
                if (username.equals("") || password.equals("") || repeatPassword.equals("") || email.equals("") || firstName.equals("") || lastName.equals("")) {
                    mUsername.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mEmail.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mPassword.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mRepeatPassword.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mEmail.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mFirstName.setHintTextColor(getResources().getColor(R.color.lightRed));
                    mLastName.setHintTextColor(getResources().getColor(R.color.lightRed));

                } else {
                    fullName = firstName+" "+lastName;

                    //Check Repeat Password is matched
                    if(password.equals(repeatPassword)){
                        //Success to register new user

                        //Check email format
                        if(!LoginSetting.emailvalidation(email)){
                            Toast.makeText(mContext, "Email is not correct format", Toast.LENGTH_SHORT).show();
                        }else {
                            try {

                                String url = "http://10.1.237.23/Han/Han/register.php";


                                //communicated with php code with http post method
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);

                                                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                                    //After Register go to Email Confirmation Activity
                                                    if (jsonObject.getString("status").equals("200")) {
                                                        String[] data = {email,jsonObject.getString("token")};
                                                        Intent intent = new Intent(mContext, EmailConfirmationActivity.class).putExtra("data",data);
                                                        startActivityForResult(intent, REQUEST_EXIT);
                                                    }


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
                                        params.put("username", username);
                                        params.put("password", password);
                                        params.put("email", email);
                                        params.put("fullname", fullName);
                                        return params;
                                    }
                                };

                                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                                requestQueue.add(stringRequest);


                            } catch (Exception e) {
                                new String("Exception: " + e.getMessage());
                                Log.d(TAG, new String("Exception: " + e.getMessage()));

                            }
                        }
                    }else{
                        Toast.makeText(mContext, "Password is not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                this.finish();

            }
        }
    }


    //initialize widget
    private void initWidget(){
        Log.d(TAG, "initWidget");

        mContext = RegisterActivity.this;

        //get EditTexts
        mUsername = findViewById(R.id.input_username_signup);
        mPassword = findViewById(R.id.input_password_signup);
        mRepeatPassword = findViewById(R.id.input_repeat_password_signup);
        mEmail = findViewById(R.id.input_email_signup);
        mFirstName = findViewById(R.id.input_firstname_signup);
        mLastName = findViewById(R.id.input_lastname_signup);

        //set EditText change hint to black
        setEditText(mUsername);
        setEditText(mPassword);
        setEditText(mRepeatPassword);
        setEditText(mEmail);
        setEditText(mFirstName);
        setEditText(mLastName);

        //get buttons
        cancelBtn = findViewById(R.id.btn_cancel_signup);
        signUpBtn = findViewById(R.id.btn_signup_signup);

    }

    private void changeHintColorToGrey(){
        mEmail.setHintTextColor(getResources().getColor(R.color.grey));
        mPassword.setHintTextColor(getResources().getColor(R.color.grey));
        mRepeatPassword.setHintTextColor(getResources().getColor(R.color.grey));
        mUsername.setHintTextColor(getResources().getColor(R.color.grey));
        mFirstName.setHintTextColor(getResources().getColor(R.color.grey));
        mLastName.setHintTextColor(getResources().getColor(R.color.grey));

    }

}
