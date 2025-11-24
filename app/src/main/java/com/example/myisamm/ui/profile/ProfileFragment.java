package com.example.myisamm.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myisamm.LoginActivity;
import com.example.myisamm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment implements OnMapReadyCallback {

    private TextView usernameTextView;
    private TextView emailTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MapView mapView;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private Bundle savedInstanceStateRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameTextView = view.findViewById(R.id.profile_username);
        emailTextView = view.findViewById(R.id.profile_email);
        MaterialButton logoutButton = view.findViewById(R.id.button_logout);

        mapView = view.findViewById(R.id.mapView);
        savedInstanceStateRef = savedInstanceState;

        loadUserData();

        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mapView != null) {
            Bundle mapViewBundle = null;
            if (savedInstanceStateRef != null) {
                mapViewBundle = savedInstanceStateRef.getBundle(MAP_VIEW_BUNDLE_KEY);
            }
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);
        }
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");

                        usernameTextView.setText(username != null ? username : "Nom non défini");
                        emailTextView.setText(email != null ? email : "Email non défini");
                    } else {
                        Toast.makeText(getContext(), "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur de chargement du profil", Toast.LENGTH_SHORT).show();
                });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMinZoomPreference(15.0f);

         //ISAMM location
        LatLng isammLocation = new LatLng(36.816, 10.06);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(isammLocation, 15.0f));
        googleMap.addMarker(new MarkerOptions().position(isammLocation).title("ISAMM"));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            Bundle mapViewBundle = new Bundle();
            mapView.onSaveInstanceState(mapViewBundle);
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
    }
}
