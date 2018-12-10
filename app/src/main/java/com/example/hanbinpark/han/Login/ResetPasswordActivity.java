package com.example.hanbinpark.han.Login;

import android.content.Context;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends LoginSetting {
    private static final String TAG = "ResetPasswordActivity";

    private Context mContext;
    private EditText mEmail;
    private String email;
    private Button btnResetPass, btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Log.d(TAG, "onCreate: started");

        mContext = this;
        mEmail = findViewById(R.id.input_email_password);
        btnResetPass = findViewById(R.id.btn_reset_password);
        btnCancel = findViewById(R.id.btn_cancel_reset_password);

        //Change hint color
        setEditText(mEmail);

        //activate Reset Password listener
        setBtnResetPass();

        //Set Cancel button listener
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Hide keyboard and focus, when tap outside of editText
        findViewById(R.id.linearLayout_reset_password).setOnTouchListener(new View.OnTouchListener() {
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

    //Set Reset password button listener
    private void setBtnResetPass(){
        btnResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();

                if(email.equals("")){
                    mEmail.setHintTextColor(getResources().getColor(R.color.lightRed));
                }else{
                    //Success to reset password

                    //Check email format
                    if(!LoginSetting.emailvalidation(email)){
                        Toast.makeText(mContext, "Email is not correct format", Toast.LENGTH_SHORT).show();
                    }else {
                        try {

                            String url = "http://10.1.237.23/Han/Han/resetPassword.php";

                            //communicated with php code by http post method
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);

                                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                                if (jsonObject.getString("status").equals("200")) {
                                                    finish();
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
                                    params.put("email", email);
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
                }
            }
        });
    }

}
