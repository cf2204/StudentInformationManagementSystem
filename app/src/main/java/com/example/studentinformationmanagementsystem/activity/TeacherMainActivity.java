package com.example.studentinformationmanagementsystem.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.TeacherDAO;
import com.example.studentinformationmanagementsystem.entity.Teacher;

public class TeacherMainActivity extends AppCompatActivity {
    private long teacher_id;//老师id
    private TeacherDAO teacherDAO;//数据库老师表操作对象
    private Teacher user;//用户对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_main);
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
        teacherDAO = new TeacherDAO(this);
        // 获取传递过来的 Intent,并获得id,默认值为 0
        Intent intent = getIntent();
        teacher_id = intent.getLongExtra("teacher_id", 0);
        if(teacher_id > 0){
            teacherDAO.open();
            user = teacherDAO.findById(teacher_id);
            teacherDAO.close();
        }else{
            user = new Teacher();
        }
    }
}