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
import com.example.myisamm.model.CategoryItem;
import com.example.myisamm.ui.courses.adapters.CategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategorySelectionFragment extends Fragment {

    private static final String TAG = "CategorySelectionFrag";

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryItemsList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyTextView;

    private String firebasePath;
    private String currentLevel;
    private String fragmentTitle; // Received from arguments, used by NavController for Toolbar

    private ValueEventListener categoryValueListener;
    private DatabaseReference databaseReference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firebasePath = getArguments().getString("firebasePath");
            fragmentTitle = getArguments().getString("title"); // Nav component uses this for label
            currentLevel = getArguments().getString("currentLevel");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.categories_recycler_view);
        progressBar = view.findViewById(R.id.categories_progress_bar);
        emptyTextView = view.findViewById(R.id.categories_empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(categoryItemsList, item -> handleCategoryClick(item, view));
        recyclerView.setAdapter(adapter);

        if (firebasePath != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(firebasePath);
            fetchCategories();
        } else {
            Log.e(TAG, "Firebase path is null!");
            if(getContext() != null) Toast.makeText(getContext(), R.string.error_loading_data, Toast.LENGTH_LONG).show();
            showEmptyView(getString(R.string.error_loading_data));
        }
    }

    private void fetchCategories() {
        showLoading(true);
        Log.d(TAG, "Fetching data from: " + firebasePath + " for level: " + currentLevel);

        if (categoryValueListener != null && databaseReference != null) { // Remove previous listener if any
            databaseReference.removeEventListener(categoryValueListener);
        }

        categoryValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryItemsList.clear();
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "No data found at " + firebasePath);
                    showEmptyView(getString(R.string.no_items_found));
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String id = snapshot.getKey();
                        String name = null;

                        if ("materialTypes".equals(currentLevel)) {
                            // For materialTypes, the key IS the name (e.g., "COURS", "TP")
                            name = id;
                        } else if (snapshot.child("name").exists()) {
                            name = snapshot.child("name").getValue(String.class);
                        } else {
                            Log.w(TAG, "Snapshot " + id + " has no 'name' child, using key as name for level " + currentLevel);
                            name = id; // Fallback, might be okay for some structures
                        }

                        if (id != null && name != null) {
                            categoryItemsList.add(new CategoryItem(id, name));
                        } else {
                            Log.w(TAG, "Missing id or name for snapshot: " + snapshot.getKey() + " at path " + firebasePath);
                        }
                    }
                    if (categoryItemsList.isEmpty()) {
                        showEmptyView(getString(R.string.no_items_found));
                    } else {
                        showEmptyView(null);
                    }
                }
                adapter.notifyDataSetChanged();
                showLoading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showLoading(false);
                Log.e(TAG, "FirebaseError: " + databaseError.getMessage() + " at path " + firebasePath);
                if(getContext() != null) Toast.makeText(getContext(), getString(R.string.error_loading_data) + ": " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyView(getString(R.string.error_loading_data));
            }
        };
        databaseReference.addValueEventListener(categoryValueListener);
    }

    private void handleCategoryClick(CategoryItem selectedItem, View view) {
        String nextFirebasePath;
        String nextTitle;
        String nextLevel;
        NavController navController = Navigation.findNavController(view);
        Bundle args = new Bundle();

        Log.d(TAG, "Current level: " + currentLevel + ", Selected item: " + selectedItem.getName() + " (ID: " + selectedItem.getId() + ")");

        switch (currentLevel) {
            case "programs":
                nextFirebasePath = firebasePath + "/" + selectedItem.getId() + "/years";
                nextTitle = selectedItem.getName() + " - Années";
                nextLevel = "years";
                args.putString("firebasePath", nextFirebasePath);
                args.putString("title", nextTitle);
                args.putString("currentLevel", nextLevel);
                navController.navigate(R.id.action_category_to_self, args);
                break;
            case "years":
                nextFirebasePath = firebasePath + "/" + selectedItem.getId() + "/semesters";
                nextTitle = selectedItem.getName() + " - Semestres";
                nextLevel = "semesters";
                args.putString("firebasePath", nextFirebasePath);
                args.putString("title", nextTitle);
                args.putString("currentLevel", nextLevel);
                navController.navigate(R.id.action_category_to_self, args);
                break;
            case "semesters":
                nextFirebasePath = firebasePath + "/" + selectedItem.getId() + "/courses";
                nextTitle = selectedItem.getName() + " - Matières"; // "Matières" instead of "Cours" for courses list
                nextLevel = "courses";
                args.putString("firebasePath", nextFirebasePath);
                args.putString("title", nextTitle);
                args.putString("currentLevel", nextLevel);
                navController.navigate(R.id.action_category_to_self, args);
                break;
            case "courses":
                nextFirebasePath = firebasePath + "/" + selectedItem.getId() + "/materials";
                nextTitle = selectedItem.getName() + " - Types";
                nextLevel = "materialTypes";
                args.putString("firebasePath", nextFirebasePath);
                args.putString("title", nextTitle);
                args.putString("currentLevel", nextLevel);
                navController.navigate(R.id.action_category_to_self, args);
                break;
            case "materialTypes":
                String pathToMaterials = firebasePath + "/" + selectedItem.getId(); // selectedItem.getId() is "COURS", "TP" etc.
                nextTitle = selectedItem.getName(); // Title will be "COURS", "TP", etc.
                args.putString("firebasePath", pathToMaterials);
                args.putString("title", nextTitle);
                navController.navigate(R.id.action_category_to_material_list, args);
                break;
            default:
                if(getContext() != null) Toast.makeText(getContext(), "Navigation level not defined: " + currentLevel, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            if (emptyTextView != null) emptyTextView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(String message) {
        if (emptyTextView != null) {
            if (message != null) {
                emptyTextView.setText(message);
                emptyTextView.setVisibility(View.VISIBLE);
                if (recyclerView != null) recyclerView.setVisibility(View.GONE);
            } else { // Hide empty view, show recycler
                emptyTextView.setVisibility(View.GONE);
                if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (categoryValueListener != null && databaseReference != null) {
            databaseReference.removeEventListener(categoryValueListener);
        }
        categoryValueListener = null; // Help GC
        databaseReference = null;
    }
}