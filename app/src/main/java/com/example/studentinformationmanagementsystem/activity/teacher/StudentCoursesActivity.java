package com.example.studentinformationmanagementsystem.activity.teacher;


import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.TranscriptDAO;
import com.example.studentinformationmanagementsystem.entity.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentCoursesActivity extends AppCompatActivity {

    private ListView coursesListView;
    private CourseDAO courseDAO;
    private TranscriptDAO transcriptDAO;
    private long studentId;
    private TextView tvStudentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_courses);

        // 获取学生ID
        studentId = getIntent().getLongExtra("student_id", -1);
        if (studentId == -1) {
            Toast.makeText(this, "无效的学生ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化DAO
        courseDAO = new CourseDAO(this);
        transcriptDAO = new TranscriptDAO(this);
        courseDAO.open();
        transcriptDAO.open();

        // 初始化视图
        tvStudentName = findViewById(R.id.tv_student_name);
        coursesListView = findViewById(R.id.courses_list_view);

        // 加载数据
        loadStudentCourses();
    }

    private void loadStudentCourses() {
        // 获取学生选修的所有课程
        List<Course> courses = courseDAO.findCoursesByStudentId(studentId);

        // 准备列表数据
        List<Map<String, String>> data = new ArrayList<>();
        for (Course course : courses) {
            Map<String, String> map = new HashMap<>();
            map.put("courseName", course.getCourseName());
            map.put("credit", String.valueOf(course.getCredit()));

            // 获取成绩
            String score = transcriptDAO.findGradeByStudentAndCourse(studentId, course.getCourseId());
            map.put("score", score != null ? score : "未评分");

            data.add(map);
        }

        // 创建适配器
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_student_course,
                new String[]{"courseName", "credit", "score"},
                new int[]{R.id.tv_course_name, R.id.tv_credit, R.id.tv_score}
        );

        coursesListView.setAdapter(adapter);

        // 设置学生姓名（需要从StudentDAO获取）
        // 这里假设我们已经通过其他方式获取了学生姓名
        // tvStudentName.setText(studentName);
    }

    @Override
    protected void onDestroy() {
        courseDAO.close();
        transcriptDAO.close();
        super.onDestroy();
    }
}