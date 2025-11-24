package com.example.myisamm.ui.courses;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myisamm.R;
import com.example.myisamm.model.MaterialItem;
import com.example.myisamm.ui.courses.adapters.MaterialAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MaterialListFragment extends Fragment {

    private static final String TAG = "MaterialListFragment";

    private RecyclerView recyclerView;
    private MaterialAdapter adapter;
    private List<MaterialItem> materialItemsList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView emptyTextView;

    private String firebasePath;
    private ValueEventListener materialsValueListener;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firebasePath = getArguments().getString("firebasePath");

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.materials_recycler_view);
        progressBar = view.findViewById(R.id.materials_progress_bar);
        emptyTextView = view.findViewById(R.id.materials_empty_text);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MaterialAdapter(materialItemsList, this::handleMaterialClick);
        recyclerView.setAdapter(adapter);

        if (firebasePath != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(firebasePath);
            fetchMaterials();
        } else {
            Log.e(TAG, "Firebase path for materials is null!");
            if(getContext() != null) Toast.makeText(getContext(), R.string.error_loading_data, Toast.LENGTH_LONG).show();
            showEmptyView(getString(R.string.error_loading_data));
        }
    }

    private void fetchMaterials() {
        showLoading(true);
        Log.d(TAG, "Fetching materials from: " + firebasePath);

        if (materialsValueListener != null && databaseReference != null) {
            databaseReference.removeEventListener(materialsValueListener);
        }

        materialsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                materialItemsList.clear();
                if (!dataSnapshot.exists()) {
                    showEmptyView(getString(R.string.no_items_found));
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MaterialItem item = snapshot.getValue(MaterialItem.class);
                        if (item != null) {
                            item.setId(snapshot.getKey()); // Set ID from snapshot key
                            materialItemsList.add(item);
                        }
                    }
                    if (materialItemsList.isEmpty()) {
                        showEmptyView(getString(R.string.no_items_found));
                    } else {
                        showEmptyView(null); // Hide empty view if items found
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
        databaseReference.addValueEventListener(materialsValueListener);
    }

    private void handleMaterialClick(MaterialItem item) {
        if (item.getType() == null) {
            Log.w(TAG, "Material item type is null for item: " + item.getName());
            if(getContext() != null) Toast.makeText(getContext(), "Type de matériel inconnu.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("LINK".equalsIgnoreCase(item.getType())) {
            if (item.getUrl() != null && !item.getUrl().isEmpty()) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Could not open link: " + item.getUrl(), e);
                    if(getContext() != null) Toast.makeText(getContext(), R.string.could_not_open_link, Toast.LENGTH_SHORT).show();
                }
            } else {
                if(getContext() != null) Toast.makeText(getContext(), R.string.link_url_missing, Toast.LENGTH_SHORT).show();
            }
        } else if ("FILE".equalsIgnoreCase(item.getType())) {
            if (item.getStoragePath() != null && !item.getStoragePath().isEmpty()) {
                downloadAndOpenFile(item.getStoragePath(), item.getName());
            } else {
                if(getContext() != null) Toast.makeText(getContext(), R.string.file_path_missing, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "Unknown material type: " + item.getType() + " for item: " + item.getName());
            if(getContext() != null) Toast.makeText(getContext(), "Type de fichier non supporté: " + item.getType(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAndOpenFile(String storagePath, String fileName) {
        if (getContext() == null) return;

        StorageReference fileRef = FirebaseStorage.getInstance().getReference(storagePath);
        Toast.makeText(getContext(), String.format(getString(R.string.downloading_file), fileName), Toast.LENGTH_SHORT).show();

        // Option 1: Use DownloadManager (downloads to public Downloads folder, shows system notification)
        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            if (downloadManager == null) {
                Toast.makeText(getContext(), R.string.download_manager_not_available, Toast.LENGTH_SHORT).show();
                return;
            }
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // Sanitize filename if necessary, or ensure it has an extension for proper opening
            String finalFileName = fileName; // Basic name, refine if needed
            if (!finalFileName.contains(".")) { // Basic check for extension
                // Try to get extension from storagePath or assume pdf/docx etc. if common
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, finalFileName);
            request.setTitle(finalFileName);
            request.setDescription(getString(R.string.downloading_file, finalFileName));
            downloadManager.enqueue(request);

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), String.format(getString(R.string.download_failed), e.getMessage()), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Download URL fetch failed for " + storagePath, e);
        });


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
        if (materialsValueListener != null && databaseReference != null) {
            databaseReference.removeEventListener(materialsValueListener);
        }
        materialsValueListener = null;
        databaseReference = null;
    }
}