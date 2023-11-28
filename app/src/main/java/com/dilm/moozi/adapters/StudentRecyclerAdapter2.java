package com.dilm.moozi.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dilm.moozi.R;
import com.dilm.moozi.models.student.Student;

import java.util.List;

public abstract class StudentRecyclerAdapter2 extends RecyclerView.Adapter<StudentRecyclerAdapter2.StudentViewHolder> {

    private List<Student> studentList;

    public StudentRecyclerAdapter2(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentRecyclerAdapter2.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_recycler2, parent, false);
        return new StudentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRecyclerAdapter2.StudentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvStudentName.setText(studentList.get(position).getName());
        if (studentList.get(position).isAttendance()) {
            holder.imgAttendanceCircle.setVisibility(View.VISIBLE);
        } else {
            holder.imgAttendanceCircle.setVisibility(View.GONE);
        }
        holder.imgViewRemoveStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                studentList.remove(position);
//                notifyItemRemoved(position);
                onRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStudentName;
        private ImageView imgViewRemoveStu, imgAttendanceCircle;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = (TextView) itemView.findViewById(R.id.tv_student_name);
            imgViewRemoveStu = (ImageView) itemView.findViewById(R.id.img_view_remove_stu);
            imgAttendanceCircle = (ImageView) itemView.findViewById(R.id.img_attendance_circle);
        }
    }

    public abstract void onRemoved(int position);
}
