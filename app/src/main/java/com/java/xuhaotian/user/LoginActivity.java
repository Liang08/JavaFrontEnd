package com.java.xuhaotian.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.java.xuhaotian.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etUser, etPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }
    public void initViews(){
        etUser = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);


    }
}