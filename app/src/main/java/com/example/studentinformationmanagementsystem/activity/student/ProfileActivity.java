package com.example.studentinformationmanagementsystem.activity.student;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.entity.Student;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvStudentId, tvName, tvGender, tvBirthDate, tvClass, tvMajor;
    private long studentId;
    private StudentDAO studentDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        initData();
        loadStudentInfo();
    }

    private void initViews() {
        tvStudentId = findViewById(R.id.tv_student_id);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvBirthDate = findViewById(R.id.tv_birth_date);
        tvClass = findViewById(R.id.tv_class);
        tvMajor = findViewById(R.id.tv_major);
    }

    private void initData() {
        studentDAO = new StudentDAO(this);
        studentId = getIntent().getLongExtra("student_id", 0);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadStudentInfo() {
        new AsyncTask<Void, Void, Student>() {
            @Override
            protected Student doInBackground(Void... voids) {
                studentDAO.open();
                Student student = studentDAO.findById(studentId);
                studentDAO.close();
                return student;
            }

            @Override
            protected void onPostExecute(Student student) {
                if (student != null) {
                    populateData(student);
                } else {
                    Toast.makeText(ProfileActivity.this, "信息加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void populateData(Student student) {
        tvStudentId.setText(String.valueOf(student.getStudentId()));
        tvName.setText(student.getName());
        tvGender.setText(student.getGender());
        tvBirthDate.setText(student.getBirthDate());
        tvClass.setText(student.getStudentClass());
        tvMajor.setText(student.getMajor());
    }
}