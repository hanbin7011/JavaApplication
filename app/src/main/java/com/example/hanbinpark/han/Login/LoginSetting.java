package com.example.hanbinpark.han.Login;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.hanbinpark.han.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSetting extends AppCompatActivity{

    //Email regex
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    //Set up EditText hint color to red when button clicked
    public void setEditText(EditText editText){
        final EditText tmpEdtText = editText;
        tmpEdtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tmpEdtText.setHintTextColor(getResources().getColor(R.color.grey));
            }
        });

    }

    public static boolean emailvalidation(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }


}
