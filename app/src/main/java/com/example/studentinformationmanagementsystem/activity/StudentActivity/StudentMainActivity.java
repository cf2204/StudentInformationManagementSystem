package com.example.studentinformationmanagementsystem.activity.StudentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.entity.Student;
import com.example.studentinformationmanagementsystem.util.GradeReportService;

public class StudentMainActivity extends AppCompatActivity {
    private long student_id;
    private Student user;
    private StudentDAO studentDAO;
    private GradeReportService gradeReportService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_main);

        initData();
        setupUI();
        setupButtonListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initData() {
        studentDAO = new StudentDAO(this);
        gradeReportService = new GradeReportService(this);
        student_id = getIntent().getLongExtra("student_id", 0);

        if (student_id > 0) {
            studentDAO.open();
            user = studentDAO.findById(student_id);
            studentDAO.close();
        } else {
            Toast.makeText(this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupUI() {
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        if (user != null) {
            tvWelcome.setText(String.format("欢迎您，%s同学", user.getName()));
        }
    }

    private void setupButtonListeners() {
        // 个人信息
        findViewById(R.id.btn_profile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("student_id", student_id);
            startActivity(intent);
        });

        // 课程管理
        findViewById(R.id.btn_courses).setOnClickListener(v -> {
            Log.d("DEBUG", "点击课程管理按钮");
            try {
                Intent intent = new Intent(this, CourseSelectionActivity.class);
                Log.d("DEBUG", "Student ID: " + student_id);
                intent.putExtra("student_id", student_id);
                startActivity(intent);
                Log.d("DEBUG", "成功启动CourseSelectionActivity");
            } catch (Exception e) {
                Log.e("DEBUG", "启动失败: " + e.getMessage());
            }
        });

        // 成绩查询
        findViewById(R.id.btn_grades).setOnClickListener(v -> {
            Intent intent = new Intent(this, GradeActivity.class);
            intent.putExtra("student_id", student_id);
            startActivity(intent);
        });

        // 导出数据
        findViewById(R.id.btn_export).setOnClickListener(v -> exportStudentData());
    }

    private void exportStudentData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        String filePath = gradeReportService.exportGradeReportToCSV(student_id);

        if (filePath != null) {
            Toast.makeText(this, "成绩单已导出至: " + filePath, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "导出成绩单失败", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (studentDAO != null) {
            studentDAO.close();
        }
    }
}