package com.dilm.moozi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dilm.moozi.R;
import com.dilm.moozi.models.student.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.StudentViewHolder> implements Filterable {

    private List<Student> studentList, studentEntireList;
    private List<Student> selectedStudentList;

    public StudentRecyclerAdapter(List<Student> studentList, List<Student> selectedStudentList) {
        this.studentList = studentList;
        this.studentEntireList = studentList;
        this.selectedStudentList = selectedStudentList;
    }

    @NonNull
    @Override
    public StudentRecyclerAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_reycler, parent, false);
        return new StudentViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRecyclerAdapter.StudentViewHolder holder, int position) {
        holder.tvStudentName.setText(studentList.get(position).getName());
        holder.cbStudent.setChecked(studentList.get(position).isSelected());
        if (holder.cbStudent.isChecked()) {
            selectedStudentList.add(studentList.get(position));
        } else {
            selectedStudentList.remove(studentList.get(position));
        }
        holder.cbStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbStudent.isChecked()) {
                    selectedStudentList.add(studentList.get(position));
                } else {
                    selectedStudentList.remove(studentList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = studentEntireList;
                    filterResults.count = studentEntireList.size();
                } else {
                    List<Student> searchStudentList = new ArrayList<>();
                    for (Student student :
                            studentEntireList) {
                        if (student.getStudentId().contains(charString)) {
                            searchStudentList.add(student);
                        }
                    }
                    filterResults.values = searchStudentList;
                    filterResults.count = searchStudentList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.values != null) {
                    studentList = (ArrayList<Student>) filterResults.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvStudentName;
        private final CheckBox cbStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            cbStudent = itemView.findViewById(R.id.checkbox_student_select);
        }
    }
}
