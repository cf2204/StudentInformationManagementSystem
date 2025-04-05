package com.example.studentinformationmanagementsystem.activity.teacher;



import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.dao.TranscriptDAO;
import com.example.studentinformationmanagementsystem.entity.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseStudentsActivity extends AppCompatActivity {

    private ListView studentListView;
    private StudentDAO studentDAO;
    private TranscriptDAO transcriptDAO;
    private long courseId;
    private List<Student> students;
    private CourseStudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_students);

        courseId = getIntent().getLongExtra("course_id", -1);
        if (courseId == -1) {
            Toast.makeText(this, "课程ID无效", Toast.LENGTH_SHORT).show();
            finish();
        }

        studentDAO = new StudentDAO(this);
        transcriptDAO = new TranscriptDAO(this);
        studentDAO.open();
        transcriptDAO.open();

        studentListView = findViewById(R.id.student_list_view);

        loadCourseStudents();
    }

    private void loadCourseStudents() {
        students = studentDAO.findStudentsByCourseId(courseId);
        adapter = new CourseStudentAdapter(this, students, courseId);
        studentListView.setAdapter(adapter);
    }

    private class CourseStudentAdapter extends BaseAdapter {
        private Context context;
        private List<Student> students;
        private long courseId;
        private LayoutInflater inflater;

        public CourseStudentAdapter(Context context, List<Student> students, long courseId) {
            this.context = context;
            this.students = students;
            this.courseId = courseId;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return students.size();
        }

        @Override
        public Student getItem(int position) {
            return students.get(position);
        }

        @Override
        public long getItemId(int position) {
            return students.get(position).getStudentId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_course_student, parent, false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.tv_student_name);
                holder.tvClass = convertView.findViewById(R.id.tv_student_class);
                holder.etScore = convertView.findViewById(R.id.et_score);
                holder.btnSave = convertView.findViewById(R.id.btn_save_score);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Student student = getItem(position);
            holder.tvName.setText(student.getName());
            holder.tvClass.setText(student.getStudentClass());

            // 加载已有成绩
            String score = transcriptDAO.findGradeByStudentAndCourse(
                    student.getStudentId(), courseId);
            holder.etScore.setText(score != null ? score : "");

            // 保存按钮点击事件
            holder.btnSave.setOnClickListener(v -> {
                String newScore = holder.etScore.getText().toString().trim();
                if (newScore.isEmpty()) {
                    Toast.makeText(context, "请输入成绩", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 检查是否已选课
                boolean isEnrolled = transcriptDAO.isCourseEnrolled(
                        student.getStudentId(), courseId);

                if (isEnrolled) {
                    // 更新成绩
                    int updated = transcriptDAO.update(
                            student.getStudentId(), courseId, newScore);
                    if (updated > 0) {
                        Toast.makeText(context, "成绩更新成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 新增成绩记录
                    long inserted = transcriptDAO.insert(
                            student.getStudentId(), courseId, newScore);
                    if (inserted != -1) {
                        Toast.makeText(context, "成绩录入成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "录入失败", Toast.LENGTH_SHORT).show();
                    }
                }

                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.etScore.getWindowToken(), 0);
            });

            return convertView;
        }

        class ViewHolder {
            TextView tvName;
            TextView tvClass;
            EditText etScore;
            Button btnSave;
        }
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        transcriptDAO.close();
        super.onDestroy();
    }
}