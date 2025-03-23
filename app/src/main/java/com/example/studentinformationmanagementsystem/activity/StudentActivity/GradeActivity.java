package com.example.studentinformationmanagementsystem.activity.StudentActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.TranscriptDAO;
import com.example.studentinformationmanagementsystem.entity.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GradeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GradeAdapter adapter;
    private long studentId;
    private CourseDAO courseDAO;
    private TranscriptDAO transcriptDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        initData();
        setupRecyclerView();
        loadGradeData();
    }

    private void initData() {
        courseDAO = new CourseDAO(this);
        transcriptDAO = new TranscriptDAO(this);
        studentId = getIntent().getLongExtra("student_id", -1);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_grades);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GradeAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadGradeData() {
        new AsyncTask<Void, Void, List<courseGrade>>() {
            @Override
            protected List<courseGrade> doInBackground(Void... voids) {
                courseDAO.open();
                transcriptDAO.open();

                List<Course> courses = courseDAO.findCoursesByStudentId(studentId);
                List<courseGrade> grades = new ArrayList<>();

                for (Course course : courses) {
                    String score = transcriptDAO.findGradeByStudentAndCourse(
                            studentId, course.getCourseId());
                    grades.add(new courseGrade(
                            course.getCourseName(),
                            course.getCredit(),
                            score != null ? score : "未知"
                    ));
                }

                courseDAO.close();
                transcriptDAO.close();
                return grades;
            }

            @Override
            protected void onPostExecute(List<courseGrade> grades) {
                adapter.updateGrades(grades);
            }
        }.execute();
    }

    public static class CourseGrade {
        private final String courseName;
        private final int credit;
        private final String score;

        public CourseGrade(String courseName, int credit, String score) {
            this.courseName = courseName;
            this.credit = credit;
            this.score = score;
        }

        // Constructor、Getters...

    }

    private static class GradeAdapter extends RecyclerView.Adapter<GradeViewHolder> {
        private List<courseGrade> grades;

        public GradeAdapter(List<courseGrade> grades) {
            this.grades = new ArrayList<>(grades);
        }

        @NonNull
        @Override
        public GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_grade, parent, false);
            return new GradeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GradeViewHolder holder, int position) {
            courseGrade grade = grades.get(position);
            holder.bind(grade);
        }

        @Override
        public int getItemCount() {
            return grades.size();
        }

        public void updateGrades(List<courseGrade> newGrades) {
            grades.clear();
            grades.addAll(newGrades);
            notifyDataSetChanged();
        }
    }

    private static class GradeViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCourseName;
        private final TextView tvCredit;
        private final TextView tvScore;

        public GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tv_course_name);
            tvCredit = itemView.findViewById(R.id.tv_credit);
            tvScore = itemView.findViewById(R.id.tv_score);
        }

        public void bind(courseGrade grade) {
            tvCourseName.setText(grade.getCourseName());
            tvCredit.setText(String.format(Locale.getDefault(), "学分：%d", grade.getCredit()));

            // 设置成绩显示样式
            String score = grade.getScore();
            tvScore.setText(String.format("成绩：%s", score));

            // 根据成绩设置颜色
            try {
                int scoreValue = Integer.parseInt(score);
                int colorRes = scoreValue >= 60 ? R.color.grade_pass : R.color.grade_fail;
                tvScore.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
            } catch (NumberFormatException e) {
                tvScore.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.grade_unknown));
            }
        }
    }

    public static class courseGrade {
        private final String courseName;
        private final int credit;
        private final String score;

        public courseGrade(String courseName, int credit, String score) {
            this.courseName = courseName;
            this.credit = credit;
            this.score = score;
        }

        // Getters
        public String getCourseName() { return courseName; }
        public int getCredit() { return credit; }
        public String getScore() { return score; }
    }
}
