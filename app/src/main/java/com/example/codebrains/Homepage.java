package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.codebrains.job.JobActivity;
import com.example.codebrains.services.FirebaseConnectionService;
import com.example.codebrains.services.FirebaseProposalListenerService;
import com.google.firebase.auth.FirebaseAuth;

import com.example.codebrains.messaging.Chat;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.codebrains.databinding.ActivityHomepageBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Homepage extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomepageBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFirebase();
        setupToolbar();
        setupFloatingActionButton();
        setupNavigation();
    }

    private void initializeFirebase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.appBarHomepage.toolbar);
    }

    private void setupFloatingActionButton() {
        binding.appBarHomepage.fab.setOnClickListener(view -> {
            startActivity(new Intent(Homepage.this, AIchatbot.class));
        });
    }

    private void setupNavigation() {
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homepage);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            DrawerLayout drawerLayout = binding.drawerLayout;

            if (id == R.id.post) {
                startActivity(new Intent(this, JobpostingActivity.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.chat) {
                startActivity(new Intent(this, Chat.class));
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.appLogOut) {
                permanentLogout();
                drawerLayout.closeDrawers();
                return true;
            }
            else if (id == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_homepage, new ProfileFragment())
                        .commit();
                return true;
            }
            else if (id == R.id.submitted) {
                Intent i=new Intent(this, Submitted_job.class);
                startActivity(i);
            }
            else if (id == R.id.nav_contact_us) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_homepage, new Contactus())
                        .commit();
                return true;
            }
            else if (id == R.id.nav_home) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_homepage, new HomeFragment())
                        .commit();
                return true;
            }

            // Let NavigationUI handle other navigation items
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.homepage, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homepage);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void joblistbtn(View view) {
        startActivity(new Intent(this, JobActivity.class));
    }

    private void permanentLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, FirebaseConnectionService.class));
        stopService(new Intent(this, FirebaseProposalListenerService.class));
        super.onDestroy();
    }
}