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

public class AddStudentActivity extends AppCompatActivity {

    private StudentDAO studentDAO;
    private EditText etName, etGender, etBirthDate, etClass, etMajor, etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        studentDAO = new StudentDAO(this);
        studentDAO.open();

        etName = findViewById(R.id.et_name);
        etGender = findViewById(R.id.et_gender);
        etBirthDate = findViewById(R.id.et_birth_date);
        etClass = findViewById(R.id.et_class);
        etMajor = findViewById(R.id.et_major);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> saveStudent());
    }

    private void saveStudent() {
        String name = etName.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String birthDate = etBirthDate.getText().toString().trim();
        String studentClass = etClass.getText().toString().trim();
        String major = etMajor.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || gender.isEmpty() || birthDate.isEmpty() ||
                studentClass.isEmpty() || major.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student();
        student.setName(name);
        student.setGender(gender);
        student.setBirthDate(birthDate);
        student.setStudentClass(studentClass);
        student.setMajor(major);

        long result = studentDAO.insertStudentWithAccount(student, username, password);
        if (result != -1) {
            Toast.makeText(this, "学生添加成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        super.onDestroy();
    }
}