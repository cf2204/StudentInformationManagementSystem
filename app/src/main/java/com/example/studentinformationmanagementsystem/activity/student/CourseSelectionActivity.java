package com.example.studentinformationmanagementsystem.activity.student;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.TeacherDAO;
import com.example.studentinformationmanagementsystem.dao.TranscriptDAO;
import com.example.studentinformationmanagementsystem.entity.Course;
import com.example.studentinformationmanagementsystem.entity.Teacher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CourseSelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private long studentId;
    private CourseDAO courseDAO;
    private TranscriptDAO transcriptDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_selection);

        try {
            initData();
            setupRecyclerView();
            loadCourseData();
        } catch (Exception e) {
            Toast.makeText(this, "初始化失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initData() {
        courseDAO = new CourseDAO(this);
        transcriptDAO = new TranscriptDAO(this);
        studentId = getIntent().getLongExtra("student_id", -1);
    }

   /* private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(new ArrayList<>(), this::handleCourseSelection);
        recyclerView.setAdapter(adapter);
    }*/

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_courses);

        // 检查RecyclerView是否正确绑定
        if (recyclerView == null) {
            Toast.makeText(this, "RecyclerView未找到", Toast.LENGTH_SHORT).show();
            return;
        }

        // 强制设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 添加临时空数据测试
        List<Course> testCourses = new ArrayList<>();
        testCourses.add(new Course(1, "测试课程", 3, 1));
        adapter = new CourseAdapter(testCourses, this::handleCourseSelection);
        recyclerView.setAdapter(adapter);

        // 检查布局是否可见
        recyclerView.setBackgroundColor(Color.RED); // 临时测试用
    }

    @SuppressLint("StaticFieldLeak")
    private void loadCourseData() {
        new AsyncTask<Void, Void, List<Course>>() {
            @Override
            protected List<Course> doInBackground(Void... voids) {
                courseDAO.open();
                transcriptDAO.open();

                List<Course> allCourses = courseDAO.findAll();

                for (Course course : allCourses) {
                    course.setSelected(transcriptDAO.isCourseEnrolled(studentId,course.getCourseId()));
                }

                courseDAO.close();
                transcriptDAO.close();
                return allCourses;
            }

            @Override
            protected void onPostExecute(List<Course> courses) {
                if (courses == null) {
                    Toast.makeText(CourseSelectionActivity.this, "课程数据为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (courses.isEmpty()) {
                    Toast.makeText(CourseSelectionActivity.this, "未查询到课程", Toast.LENGTH_SHORT).show();
                }

                Log.d("DATA_DEBUG", "加载到课程数量：" + courses.size());
                adapter.updateCourses(courses);
            }
        }.execute();
    }

    private void handleCourseSelection(Course course) {
        if (course.isSelected()) {
            unenrollCourse(course);
        } else {
            enrollCourse(course);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void enrollCourse(Course course) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    transcriptDAO.open();
                    long result = transcriptDAO.insert(studentId, course.getCourseId(), "未知");
                    return result != -1;
                } finally {
                    transcriptDAO.close();
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    course.setSelected(true);
                    adapter.notifyItemChanged(adapter.getCourses().indexOf(course));
                    Toast.makeText(CourseSelectionActivity.this, "选课成功", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void unenrollCourse(Course course) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    transcriptDAO.open();
                    int result = transcriptDAO.delete(studentId, course.getCourseId());
                    return result > 0;
                } finally {
                    transcriptDAO.close();
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    course.setSelected(false);
                    adapter.notifyItemChanged(adapter.getCourses().indexOf(course));
                    Toast.makeText(CourseSelectionActivity.this, "退选成功", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private class CourseAdapter extends RecyclerView.Adapter<CourseViewHolder> {
        private List<Course> courses;
        private final CourseClickListener clickListener;

        public CourseAdapter(List<Course> courses, CourseClickListener clickListener) {
            this.courses = new ArrayList<>(courses);
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course, parent, false);
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            Course course = courses.get(position);
            holder.bind(course);

            holder.btnAction.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    clickListener.onCourseClick(courses.get(adapterPosition));
                }
            });
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void updateCourses(List<Course> newCourses) {
            courses.clear();
            courses.addAll(newCourses);
            notifyDataSetChanged();
        }

        public List<Course> getCourses() {
            return Collections.unmodifiableList(courses);
        }
    }

    private static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseName;
        private final TextView tvCredit;
        private final TextView tvTeacher;
        private final Button btnAction;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvCredit = itemView.findViewById(R.id.tv_credit);
            tvTeacher = itemView.findViewById(R.id.tv_teacher);
            btnAction = itemView.findViewById(R.id.btn_action);
        }

        public void bind(Course course) {
            tvCourseName.setText(course.getCourseName());
            tvCredit.setText(String.format(Locale.getDefault(), "学分：%d", course.getCredit()));

            // 获取教师信息（需要根据你的TeacherDAO实现）
            TeacherDAO teacherDAO = new TeacherDAO(itemView.getContext());
            teacherDAO.open();
            Teacher teacher = teacherDAO.findById(course.getTeacherId());
            teacherDAO.close();

            tvTeacher.setText(teacher != null ? "教师：" + teacher.getName() : "未知教师");

            // 更新按钮状态
            btnAction.setText(course.isSelected() ? "退选" : "选课");
            btnAction.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),
                    course.isSelected() ? R.color.colorAccent : R.color.colorPrimary));
        }
    }

    public interface CourseClickListener {
        void onCourseClick(Course course);
    }
}