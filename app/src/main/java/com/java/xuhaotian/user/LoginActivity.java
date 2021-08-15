package com.java.xuhaotian.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.java.xuhaotian.Consts.JSON;

public class LoginActivity extends AppCompatActivity {
    private EditText etUser, etPassword;
    private Button mBtnLogin;
    private boolean password_correct = false;
    String url = "127.0.0.1:8080/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initEvent();
    }

    public void initViews(){
        etUser = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
    }

    public void initEvent(){
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUser.getText().toString();
                String password = etPassword.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (password_correct){
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}