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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hanbinpark.han.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.hanbinpark.han.Login.LoginActivity.REQUEST_EXIT;

public class EmailConfirmationActivity extends LoginSetting {
    private static final String TAG = "ResetPasswordActivity";

    private Context mContext;
    private EditText mCode;
    private TextView mEmailText;
    private String code, email, token;
    private Button btnConfirm, btnCancel, btnChangeEmail;
    public static Button btnSendEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_confirmation);
        Log.d(TAG, "onCreate: started");

        //Set up widgets
        mContext = this;
        mCode = findViewById(R.id.input_code_email_confirm);
        btnConfirm = findViewById(R.id.btn_confirm_email_confirm);
        btnCancel = findViewById(R.id.btn_cancel_email_confirm);
        btnSendEmail = findViewById(R.id.btn_sendEmail_email_confirm);
        btnChangeEmail = findViewById(R.id.btn_changeEmail_email_confirm);
        mEmailText = findViewById(R.id.TextView_email_email_confirm);

        //get Data(email, token) from parent(RegisterActivity) activity
        email = getIntent().getStringArrayExtra("data")[0];
        token = getIntent().getStringArrayExtra("data")[1];

        //Change email label Text
        mEmailText.setText("sent to " + email);


        //Change hint color
        setEditText(mCode);

        //activate All buttons
        setBtn();


        //Hide keyboard and focus, when tap outside of editText
        findViewById(R.id.relativeLayout_email_confirm).setOnTouchListener(new View.OnTouchListener() {
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

    //Set All button listeners
    private void setBtn() {
        //Email Confrimation button set up
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHintColorToGrey();
                code = mCode.getText().toString();

                // Check TextField empty or not
                if (code.equals("")) {
                    mCode.setHintTextColor(getResources().getColor(R.color.lightRed));

                    //Check code is correct, if yes delete token and finish all login activities
                } else if(code.equals(token)){
                    deleteToken(true);
                    //SetResutl ok to finish parent activity
                    setResult(RESULT_OK, null);
                    finish();
                }else{
                    Toast.makeText(mContext, "Invalid Confirmation Code", Toast.LENGTH_SHORT).show();
                }
            }

        });
        //set up cancel Button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            //If button clicked delete user from mysql
            @Override
            public void onClick(View view) {
                try {

                    String url = "http://10.1.237.23/Han/Han/deleteUser.php";

                    //communicated with php code by http post method
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.getString("status").equals("200")) {
                                            deleteToken(false);
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
        });

        //Set up send email buttons, Delete token and resend email
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String url = "http://10.1.237.23/Han/Han/sendEmail.php";

                    //communicated with php code by http post method
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);

                                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                        if (jsonObject.getString("status").equals("200")) {
                                            token = jsonObject.getString("token");
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
                            params.put("token", token);
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
        });
        //Set up change email button, go to changeEmailActivity intent
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHintColorToGrey();
                String[] data = {email,token};
                Intent intent = new Intent(mContext, ChangeEmailActivity.class).putExtra("data",data);
                startActivity(intent);
                finish();
                changeHintColorToGrey();
            }
        });
    }

    //Delete token method
    public void deleteToken(final boolean checkLogin){
        try {

            String url = "http://10.1.237.23/Han/Han/deleteToken.php";

            //communicated with php code by http post method
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

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
                    if(checkLogin){
                        params.put("token", token);
                        params.put("confirmationStatus", "1");
                    }else{
                        params.put("token", token);
                        params.put("confirmationStatus", "0");
                    }
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

    //Change hint color to grey
    private void changeHintColorToGrey(){
        mCode.setHintTextColor(getResources().getColor(R.color.grey));
    }

}
