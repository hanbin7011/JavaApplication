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

public class ChangeEmailActivity extends LoginSetting {
    private static final String TAG = "ResetPasswordActivity";

    private Context mContext;
    private EditText mEmail;
    private String newEmail, email, token;
    private Button btnChangeEmail, btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        Log.d(TAG, "onCreate: started");

        //set up widgets
        mContext = this;
        btnChangeEmail = findViewById(R.id.btn_change_email);
        mEmail = findViewById(R.id.input_email_change_email);
        btnCancel = findViewById(R.id.btn_cancel_change_email);

        //Get data(email, token) from emailConfirmationActivity
        email = getIntent().getStringArrayExtra("data")[0];
        token = getIntent().getStringArrayExtra("data")[1];

        //Change hint color
        setEditText(mEmail);

        //activate  change email button listener
        setBtnChangeEmail();

        //Set Cancel button listener, due to email Label field start new intent instead of finish()
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] data = {email,token};
                Intent intent = new Intent(mContext, EmailConfirmationActivity.class).putExtra("data",data);
                startActivity(intent);
                finish();
            }
        });

        //Hide keyboard and focus, when tap outside of editText
        findViewById(R.id.linearLayout_change_email).setOnTouchListener(new View.OnTouchListener() {
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

    //Set Change email button listener
    private void setBtnChangeEmail(){
        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHintColorToGrey();
                newEmail = mEmail.getText().toString();

                //Check textField is empty
                if(newEmail.equals("")){
                    mEmail.setHintTextColor(getResources().getColor(R.color.lightRed));
                }else if(newEmail.equals(email)) {
                    //check input newEmail is same as now email, if yes send code again
                    EmailConfirmationActivity.btnSendEmail.performClick();
                    String[] data = {newEmail,token};
                    Intent intent = new Intent(mContext, EmailConfirmationActivity.class).putExtra("data",data);
                    startActivity(intent);
                    finish();
                }else{

                    //Change Email from user table and send new Confirmation code
                    try {

                        String url = "http://10.1.237.23/Han/Han/changeEmail.php";

                        //communicated with php code by http post method
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            if (jsonObject.getString("status").equals("200")) {
                                                String[] data = {jsonObject.getString("email"),jsonObject.getString("token")};
                                                Intent intent = new Intent(mContext, EmailConfirmationActivity.class).putExtra("data",data);
                                                startActivity(intent);
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
                                    params.put("token", token);
                                    params.put("newEmail", newEmail);
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
        });
    }

    //Change hint color to grey
    private void changeHintColorToGrey(){
        mEmail.setHintTextColor(getResources().getColor(R.color.grey));
    }
}
