package com.example.studentinformationmanagementsystem.activity.teacher;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.entity.Student;

import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private ListView studentListView;
    private StudentDAO studentDAO;
    private List<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        studentDAO = new StudentDAO(this);
        studentDAO.open();

        studentListView = findViewById(R.id.student_list_view);
        Button btnAddStudent = findViewById(R.id.btn_add_student);

        // 加载学生列表
        loadStudents();

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            Student student = studentList.get(position);
            Intent intent = new Intent(ManageStudentsActivity.this, StudentDetailActivity.class);
            intent.putExtra("student_id", student.getStudentId());
            startActivity(intent);
        });

        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentsActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });
    }

    private void loadStudents() {
        studentList = studentDAO.findAll();

        // 使用自定义适配器
        StudentAdapter adapter = new StudentAdapter(this, studentList);
        studentListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents(); // 刷新列表
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        super.onDestroy();
    }

    public class StudentAdapter extends ArrayAdapter<Student> {
        public StudentAdapter(Context context, List<Student> students) {
            super(context, R.layout.item_student, students);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_student, parent, false);
            }

            Student student = getItem(position);

            TextView tvName = convertView.findViewById(R.id.tv_student_name);
            TextView tvClass = convertView.findViewById(R.id.tv_student_class);

            tvName.setText(student.getName());
            tvClass.setText(student.getStudentClass());

            return convertView;
        }
    }
}