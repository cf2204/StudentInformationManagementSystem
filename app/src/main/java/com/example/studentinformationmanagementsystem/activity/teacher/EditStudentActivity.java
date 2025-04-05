package com.example.studentinformationmanagementsystem.activity.teacher;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.entity.Student;

public class EditStudentActivity extends AppCompatActivity {

    private StudentDAO studentDAO;
    private long studentId;
    private EditText etName, etGender, etBirthDate, etClass, etMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        studentDAO = new StudentDAO(this);
        studentDAO.open();

        studentId = getIntent().getLongExtra("student_id", -1);
        if (studentId == -1) {
            Toast.makeText(this, "无效的学生ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化视图
        etName = findViewById(R.id.et_name);
        etGender = findViewById(R.id.et_gender);
        etBirthDate = findViewById(R.id.et_birth_date);
        etClass = findViewById(R.id.et_class);
        etMajor = findViewById(R.id.et_major);
        Button btnSave = findViewById(R.id.btn_save);

        // 加载学生数据
        loadStudentData();

        btnSave.setOnClickListener(v -> saveStudent());
    }

    private void loadStudentData() {
        Student student = studentDAO.findById(studentId);
        if (student != null) {
            etName.setText(student.getName());
            etGender.setText(student.getGender());
            etBirthDate.setText(student.getBirthDate());
            etClass.setText(student.getStudentClass());
            etMajor.setText(student.getMajor());
        }
    }

    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String studentClass = etClass.getText().toString().trim();
        String major = etMajor.getText().toString().trim();

        if (name.isEmpty() || gender.isEmpty() || birthDate.isEmpty() ||
                studentClass.isEmpty() || major.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(name);
        student.setGender(gender);
        student.setBirthDate(birthDate);
        student.setStudentClass(studentClass);
        student.setMajor(major);

        int result = studentDAO.update(student);
        if (result > 0) {
            Toast.makeText(this, "学生信息更新成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        super.onDestroy();
    }
}