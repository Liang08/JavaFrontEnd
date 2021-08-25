package com.java.xuhaotian.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;
import com.java.xuhaotian.mainpage.MainPageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.java.xuhaotian.Consts.JSON;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUser, etPassword;
    private Button mBtnLogin, mBtnRegister;

    private boolean password_correct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        initEvent();
    }

    public void initViews() {
        etUser = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
    }

    public void initEvent() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUser.getText().toString();
                String password = etPassword.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject json = new JSONObject();
                            json.put("userName", username);
                            json.put("password", password);
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = RequestBody.create(String.valueOf(json), JSON);
                            Request request = new Request.Builder()
                                    .url(Consts.backendURL + "register")
                                    .post(body)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response.code() == 200) {
                                Log.d("code", Consts.getToken());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        password_correct = false;
                        try {
                            JSONObject json = new JSONObject();
                            json.put("userName", username);
                            json.put("password", password);
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = RequestBody.create(String.valueOf(json), JSON);
                            Request request = new Request.Builder()
                                    .url(Consts.backendURL + "login")
                                    .post(body)
                                    .build();
                            Response response = client.newCall(request).execute();
                            if (response.code() == 200){
                                Consts.setToken(response.body().string());
                                Log.d("code", Consts.getToken());
                                password_correct = true;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (password_correct){
                    Intent intent = new Intent(RegisterActivity.this, MainPageActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}