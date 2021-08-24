package com.java.xuhaotian.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.xuhaotian.Consts;
import com.java.xuhaotian.HttpRequest;
import com.java.xuhaotian.R;

import org.json.JSONObject;

public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText etOldPassword, etNewPassword, etNewPasswordAgain;
    private Button mBtnModify, mBtnCancel;
    private boolean modify_ok = false;
    private String error_message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initViews();
        initEvent();
    }

    public void initViews() {
        etOldPassword = findViewById(R.id.et_modify_password_old_password);
        etNewPassword = findViewById(R.id.et_modify_password_new_password);
        etNewPasswordAgain = findViewById(R.id.et_modify_password_new_password_again);
        mBtnModify = findViewById(R.id.btn_modify_password_modify);
        mBtnCancel = findViewById(R.id.btn_modify_password_cancel);
    }

    public void initEvent() {
        mBtnModify.setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String newPasswordAgain = etNewPasswordAgain.getText().toString();
            if (newPassword.isEmpty()) {
                Toast.makeText(ModifyPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(newPasswordAgain)) {
                Toast.makeText(ModifyPasswordActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                modify_ok = false;
                JSONObject json = new JSONObject();
                json.put("oldPassword", oldPassword);
                json.put("newPassword", newPassword);
                json.put("token", Consts.getToken());
                HttpRequest.MyResponse response = new HttpRequest().putRequest(Consts.backendURL + "modifyPassword", json);
                if (response.code() == 200) {
                    Consts.setToken(response.string());
                    modify_ok = true;
                }
                else if (response.code() == 500) {
                    error_message = "请求失败(" + response.code() + ")";
                }
                else {
                    JSONObject obj = new JSONObject(response.string());
                    error_message = obj.getString("message");
                }
            } catch (Exception e) {
                e.printStackTrace();
                error_message = "请求异常";
            }

            if (modify_ok) {
                Toast.makeText(ModifyPasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(ModifyPasswordActivity.this, "修改失败：" + error_message, Toast.LENGTH_SHORT).show();
            }
        });
        mBtnCancel.setOnClickListener(v -> finish());
    }
}