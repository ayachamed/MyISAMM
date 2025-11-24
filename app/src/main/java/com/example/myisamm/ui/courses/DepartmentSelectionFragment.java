package com.example.myisamm.ui.courses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myisamm.R;
import com.example.myisamm.model.CategoryItem; // Using CategoryItem
import com.example.myisamm.ui.courses.adapters.DepartmentAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSelectionFragment extends Fragment {

    private static final String TAG = "DeptSelectFragment";

    private RecyclerView recyclerView;
    private DepartmentAdapter adapter;
    private List<CategoryItem> departmentItemsList;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private DatabaseReference departmentsRef;
    private ValueEventListener departmentsListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        departmentItemsList = new ArrayList<>();
        departmentsRef = FirebaseDatabase.getInstance().getReference("departments");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_department_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.departments_recycler_view);
        progressBar = view.findViewById(R.id.departments_progress_bar);
        emptyTextView = view.findViewById(R.id.departments_empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DepartmentAdapter(departmentItemsList, department -> {
            Log.d(TAG, "Clicked department: " + department.getName() + " (ID: " + department.getId() + ")");

            String firebasePath = "departments/" + department.getId() + "/programs";
            String title = department.getName() + " - Programmes";

            Bundle args = new Bundle();
            args.putString("firebasePath", firebasePath);
            args.putString("title", title);
            args.putString("currentLevel", "programs"); // Next level is 'programs'

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_department_to_category, args);
        });
        recyclerView.setAdapter(adapter);

        fetchDepartments();
    }

    private void fetchDepartments() {
        showLoading(true);
        if (departmentsListener == null) {
            departmentsListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    departmentItemsList.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String id = snapshot.getKey(); // e.g., "dept_multimedia"
                            String name = snapshot.child("name").getValue(String.class);

                            if (id != null && name != null) {
                                departmentItemsList.add(new CategoryItem(id, name));
                            } else {
                                Log.w(TAG, "Department data incomplete for key: " + snapshot.getKey());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    showLoading(false);
                    updateEmptyViewStatus();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Firebase DB Error: " + databaseError.getMessage());
                    if(getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load departments.", Toast.LENGTH_SHORT).show();
                    }
                    showLoading(false);
                    updateEmptyViewStatus();
                }
            };
        }
        departmentsRef.addValueEventListener(departmentsListener); // Use addValueEventListener for potential updates
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (!isLoading) { // Only show RecyclerView or empty text when not loading
            updateEmptyViewStatus();
        } else { // When loading, hide both
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);
        }
    }

    private void updateEmptyViewStatus() {
        if (departmentItemsList.isEmpty()) {
            if (emptyTextView != null) emptyTextView.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        } else {
            if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (departmentsRef != null && departmentsListener != null) {
            departmentsRef.removeEventListener(departmentsListener); // Clean up listener
        }
    }
}