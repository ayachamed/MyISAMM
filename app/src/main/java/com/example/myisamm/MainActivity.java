package com.example.myisamm;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable; // Added for OnDestinationChangedListener
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination; // Added for OnDestinationChangedListener
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseApp; // Usually not needed if auto-init is enabled

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();


            Set<Integer> topLevelDestinations = new HashSet<>();
            topLevelDestinations.add(R.id.navigation_home);
            topLevelDestinations.add(R.id.navigation_courses_department);
            topLevelDestinations.add(R.id.navigation_profile);
            topLevelDestinations.add(R.id.navigation_about);

            appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();


            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavView, navController);


            navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                @Override
                public void onDestinationChanged(@NonNull NavController controller,
                                                 @NonNull NavDestination destination,
                                                 @Nullable Bundle arguments) {

                    if (toolbarTitle != null && destination.getLabel() != null) {

                        if (arguments != null && destination.getLabel().toString().contains("{title}")) {
                            String dynamicTitle = arguments.getString("title");
                            if (dynamicTitle != null) {
                                toolbarTitle.setText(dynamicTitle);
                            } else {
                                toolbarTitle.setText(destination.getLabel());
                            }
                        } else {
                            toolbarTitle.setText(destination.getLabel());
                        }
                    } else if (toolbarTitle != null) {
                        // Fallback if no label
                        toolbarTitle.setText("MyISAMM");
                    }


                }
            });

        } else {
            Log.e("MainActivity", "FATAL ERROR: NavHostFragment not found! Cannot setup navigation.");
            // Consider showing an error message to the user or finishing the activity
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (navController != null) {
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (navController != null && NavigationUI.navigateUp(navController, appBarConfiguration))
                || super.onSupportNavigateUp();
    }
}