package com.example.studentinformationmanagementsystem.activity.teacher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.dao.TeacherDAO;
import com.example.studentinformationmanagementsystem.entity.Course;
import com.example.studentinformationmanagementsystem.entity.Student;
import com.example.studentinformationmanagementsystem.entity.Teacher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImportDataActivity extends AppCompatActivity {

    private static final int FILE_REQUEST_CODE = 1;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private CourseDAO courseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);

        studentDAO = new StudentDAO(this);
        teacherDAO = new TeacherDAO(this);
        courseDAO = new CourseDAO(this);

        studentDAO.open();
        teacherDAO.open();
        courseDAO.open();

        Button btnSelectFile = findViewById(R.id.btn_select_file);
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 解析CSV文件并导入数据
                        importDataFromCSV(line);
                    }

                    Toast.makeText(this, "数据导入成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "导入失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void importDataFromCSV(String csvLine) {
        // 根据CSV格式解析并导入数据
        // 格式: "type,name,gender,birth_date,class,major,username,password"
        String[] parts = csvLine.split(",");

        if (parts.length > 0) {
            String type = parts[0].trim();

            if (type.equalsIgnoreCase("student")) {
                // 导入学生数据
                if (parts.length >= 8) {
                    Student student = new Student();
                    student.setName(parts[1].trim());
                    student.setGender(parts[2].trim());
                    student.setBirthDate(parts[3].trim());
                    student.setStudentClass(parts[4].trim());
                    student.setMajor(parts[5].trim());

                    String username = parts[6].trim();
                    String password = parts[7].trim();

                    studentDAO.insertStudentWithAccount(student, username, password);
                }
            } else if (type.equalsIgnoreCase("teacher")) {
                // 导入教师数据
                if (parts.length >= 5) {
                    Teacher teacher = new Teacher();
                    teacher.setName(parts[1].trim());
                    teacher.setDepartment(parts[2].trim());

                    String username = parts[3].trim();
                    String password = parts[4].trim();

                    teacherDAO.insertTeacherWithAccount(teacher, username, password);
                }
            } else if (type.equalsIgnoreCase("course")) {
                // 导入课程数据
                if (parts.length >= 4) {
                    Course course = new Course();
                    course.setCourseName(parts[1].trim());
                    course.setCredit(Integer.parseInt(parts[2].trim()));
                    course.setTeacherId(Long.parseLong(parts[3].trim()));

                    courseDAO.insert(course);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        teacherDAO.close();
        courseDAO.close();
        super.onDestroy();
    }
}