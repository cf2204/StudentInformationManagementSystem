package com.example.studentinformationmanagementsystem.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
    Button btnManageStudents;
    Button btnManageCourses;
    Button btnImportData;
    Button btnExportData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_main);
        initData();
        initView();
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

    /**
     * 初始化界面
     */
    void initView(){
        btnManageStudents = findViewById(R.id.btn_manage_students);
        btnManageCourses = findViewById(R.id.btn_manage_courses);
        btnImportData = findViewById(R.id.btn_import_data);
        btnExportData = findViewById(R.id.btn_export_data);

        btnManageStudents.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherMainActivity.this, ManageStudentsActivity.class);
            intent.putExtra("teacher_id", teacher_id);
            startActivity(intent);
        });

        btnManageCourses.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherMainActivity.this, ManageCoursesActivity.class);
            intent.putExtra("teacher_id", teacher_id);
            startActivity(intent);
        });

        btnImportData.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherMainActivity.this, ImportDataActivity.class);
            startActivity(intent);
        });

        btnExportData.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherMainActivity.this, ExportDataActivity.class);
            intent.putExtra("teacher_id", teacher_id);
            startActivity(intent);
        });
    }
}