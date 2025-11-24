package com.example.myisamm.ui.courses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myisamm.R;
import com.example.myisamm.model.CategoryItem; // Using CategoryItem for Departments

import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentViewHolder> {

    private List<CategoryItem> departmentList;
    private OnDepartmentClickListener listener;

    public interface OnDepartmentClickListener {
        void onDepartmentClick(CategoryItem department);
    }

    public DepartmentAdapter(List<CategoryItem> departmentList, OnDepartmentClickListener listener) {
        this.departmentList = departmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_department, parent, false);
        return new DepartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position) {
        CategoryItem department = departmentList.get(position);
        holder.departmentNameTextView.setText(department.getName());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDepartmentClick(department);
            }
        });
    }

    @Override
    public int getItemCount() {
        return departmentList.size();
    }

    static class DepartmentViewHolder extends RecyclerView.ViewHolder {
        TextView departmentNameTextView;
        ImageView departmentIconImageView;

        public DepartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            departmentNameTextView = itemView.findViewById(R.id.department_name_text_view);
            departmentIconImageView = itemView.findViewById(R.id.department_icon_image_view);
        }
    }
}