package com.example.studentinformationmanagementsystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.*;
import com.example.studentinformationmanagementsystem.entity.*;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 找到布局文件中的按钮
        Button loginButton = findViewById(R.id.button); // 登录按钮
        Button registerButton = findViewById(R.id.button2); // 注册按钮

        // 设置登录按钮的点击监听器
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.button){
            // 登录学生
            // 创建 Intent，指定从 LoginActivity 跳转到 StudentActivity
            Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
            // 传递学生id
            intent.putExtra("student_id", 0);
            // 启动 Activity
            startActivity(intent);
        }
        if(id == R.id.button2){
            //登录老师
            // 创建 Intent，指定从 LoginActivity 跳转到 StudentActivity
            Intent intent = new Intent(LoginActivity.this, StudentMainActivity.class);
            // 传递学生id
            intent.putExtra("student_id", 0);
            // 启动 Activity
            startActivity(intent);
        }
        finish();
    }
}