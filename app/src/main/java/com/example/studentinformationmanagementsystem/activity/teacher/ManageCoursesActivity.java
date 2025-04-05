package com.example.studentinformationmanagementsystem.activity.teacher;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.TeacherDAO;
import com.example.studentinformationmanagementsystem.entity.Course;
import com.example.studentinformationmanagementsystem.entity.Teacher;

import java.util.List;

public class ManageCoursesActivity extends AppCompatActivity {

    private ListView courseListView;
    private CourseDAO courseDAO;
    private List<Course> courseList;
    private long teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);

        teacherId = getIntent().getLongExtra("teacher_id", -1);
        courseDAO = new CourseDAO(this);
        courseDAO.open();

        courseListView = findViewById(R.id.course_list_view);

        loadCourses();

        courseListView.setOnItemClickListener((parent, view, position, id) -> {
            Course course = courseList.get(position);
            Intent intent = new Intent(ManageCoursesActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_id", course.getCourseId());
            startActivity(intent);
        });
    }

    private void loadCourses() {
        if (teacherId != -1) {
            // 只加载该教师教授的课程
            courseList = courseDAO.findCoursesByTeacherId(teacherId);
        } else {
            courseList = courseDAO.findAll();
        }

        // 使用自定义适配器
        CourseAdapter adapter = new CourseAdapter(this, courseList);
        courseListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    @Override
    protected void onDestroy() {
        courseDAO.close();
        super.onDestroy();
    }

    // 自定义课程适配器
    private static class CourseAdapter extends ArrayAdapter<Course> {
        private TeacherDAO teacherDAO;

        public CourseAdapter(Context context, List<Course> courses) {
            super(context, R.layout.item_course2, courses);
            teacherDAO = new TeacherDAO(context);
            teacherDAO.open();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_course2, parent, false);
            }

            Course course = getItem(position);

            TextView tvName = convertView.findViewById(R.id.tv_course_name);
            TextView tvCredit = convertView.findViewById(R.id.tv_credit);
            TextView tvTeacher = convertView.findViewById(R.id.tv_teacher);

            tvName.setText(course.getCourseName());
            tvCredit.setText(course.getCredit() + "学分");

            // 获取教师姓名
            Teacher teacher = teacherDAO.findById(course.getTeacherId());
            String teacherName = teacher != null ? teacher.getName() : "未知教师";
            tvTeacher.setText("教师: " + teacherName);

            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            teacherDAO.close();
        }
    }
}