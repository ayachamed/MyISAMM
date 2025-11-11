package com.example.myisamm.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myisamm.R;
import com.example.myisamm.model.Club;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView welcomeTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ViewPager2 clubsViewPager;

    private Handler autoScrollHandler = new Handler();
    private int currentPage = 0;
    private List<Club> clubList;

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (clubsViewPager != null && clubList != null && !clubList.isEmpty()) {
                currentPage = (currentPage + 1) % clubList.size();
                clubsViewPager.setCurrentItem(currentPage, true);
                autoScrollHandler.postDelayed(this, 3000); // Scroll every 3 seconds
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        welcomeTextView = view.findViewById(R.id.home_welcome_text);
        fetchUsername();

        // Load clubs from JSON
        clubList = loadClubsJson();

        // Setup ViewPager2 for clubs
        clubsViewPager = view.findViewById(R.id.home_clubs_viewpager);
        ClubAdapter clubAdapter = new ClubAdapter(clubList, getContext());
        clubsViewPager.setAdapter(clubAdapter);


        clubsViewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0.25f + (1 - Math.abs(position)));
        });


        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);


        ImageView universityImage = view.findViewById(R.id.home_university_image);
        universityImage.setOnClickListener(v -> {
            String url = "https://isa2m.rnu.tn/";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });


        return view;
    }

    private List<Club> loadClubsJson() {
        try {
            InputStream is = getContext().getAssets().open("clubs.json");
            InputStreamReader reader = new InputStreamReader(is);
            Type listType = new TypeToken<ArrayList<Club>>() {
            }.getType();
            return new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void fetchUsername() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        if (username != null) {
                            welcomeTextView.setText("Bienvenue, " + username + "!");
                        }
                    } else {
                        Toast.makeText(getContext(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur lors de la récupération du nom", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoScrollHandler.removeCallbacks(autoScrollRunnable); // Clean up the auto-scroll handler
    }
}
