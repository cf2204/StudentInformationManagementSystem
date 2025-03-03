package com.example.studentinformationmanagementsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.*;

public class LoginActivity extends AppCompatActivity{
    private EditText usernameEditText;
    private EditText passwordEditText;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    void initData(){
        userDAO = new UserDAO(this);
    }

    /**
     * 初始化视图
     */
    void initView(){
        // 初始化视图
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        // 设置登录按钮点击事件
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            // 简单的输入验证
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            } else {
                login(username,password);
            }
        });
    }

    /**
     * 登录方法
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password) {
        userDAO.open();
        String role = userDAO.login(username,password);
        long id = userDAO.getUserIdByPassword(username,password);
        userDAO.close();
        if(role != null){
            if(role.equals("student")){
                // 登录学生
                // 创建 Intent，指定从 LoginActivity 跳转到 StudentActivity
                Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                // 传递学生id
                intent.putExtra("student_id", id);
                // 提示登录成功
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                // 启动 Activity
                startActivity(intent);
                finish();
            }else if(role.equals("teacher")){
                //登录老师
                // 创建 Intent，指定从 LoginActivity 跳转到 StudentActivity
                Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
                // 传递老师id
                intent.putExtra("teacher_id", id);
                // 提示登录成功
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                // 启动 Activity
                startActivity(intent);
                finish();
            }
        }else{
            // 提示登录失败
            Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
}