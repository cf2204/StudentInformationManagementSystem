package com.example.studentinformationmanagementsystem.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.entity.Student;

public class StudentMainActivity extends AppCompatActivity {
    private long student_id;//学生id
    private Student user;//学生用户对象
    private StudentDAO studentDAO;//数据库学生表操作对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_main);
        initData();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 初始化数据
     */
    void initData(){
        studentDAO = new StudentDAO(this);
        // 获得登录界面传递的学生id
        Intent intent = getIntent();
        student_id = intent.getLongExtra("student_id",0);
        // 根据id获得学生对象
        if(student_id > 0){
            studentDAO.open();
            user = studentDAO.findById(student_id);
            studentDAO.close();
        }else{
            user = new Student();
        }
    }
}