package com.example.studentinformationmanagementsystem.activity.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.entity.Student;

public class StudentDetailActivity extends AppCompatActivity {

    private StudentDAO studentDAO;
    private long studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        studentDAO = new StudentDAO(this);
        studentDAO.open();

        studentId = getIntent().getLongExtra("student_id", -1);

        Button btnEdit = findViewById(R.id.btn_edit);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnViewCourses = findViewById(R.id.btn_view_courses);

        displayStudentDetails();

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDetailActivity.this, EditStudentActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            int result = studentDAO.delete(studentId);
            if (result > 0) {
                Toast.makeText(this, "学生删除成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });

        btnViewCourses.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDetailActivity.this, StudentCoursesActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });
    }

    private void displayStudentDetails() {
        Student student = studentDAO.findById(studentId);
        if (student != null) {
            TextView tvName = findViewById(R.id.tv_name);
            TextView tvGender = findViewById(R.id.tv_gender);
            TextView tvBirthDate = findViewById(R.id.tv_birth_date);
            TextView tvClass = findViewById(R.id.tv_class);
            TextView tvMajor = findViewById(R.id.tv_major);

            tvName.setText(student.getName());
            tvGender.setText(student.getGender());
            tvBirthDate.setText(student.getBirthDate());
            tvClass.setText(student.getStudentClass());
            tvMajor.setText(student.getMajor());
        }
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        super.onDestroy();
    }
}