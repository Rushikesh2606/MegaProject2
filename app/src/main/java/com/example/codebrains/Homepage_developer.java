package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.codebrains.freelancer.FindjobActivity;
import com.example.codebrains.messaging.Chat;
import com.example.codebrains.services.FirebaseConnectionService;
import com.example.codebrains.services.FirebaseMessageListenerService;
import com.example.codebrains.services.FirebaseProposalListenerService;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.codebrains.databinding.ActivityHomepageDeveloperBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Homepage_developer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomepageDeveloperBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageDeveloperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomepageDeveloper.toolbar);
        binding.appBarHomepageDeveloper.fab.setOnClickListener(view -> {
            Intent intent = new Intent(Homepage_developer.this, AIchatbot.class);
            startActivity(intent);
            finish();
        });
        Intent serviceIntent = new Intent(this, JobNotificationService.class);
        startService(serviceIntent);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configure top-level destinations for the Navigation Component.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homepage_developer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Override NavigationView item selection to handle some items manually.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                boolean handled = false;

                if (id == R.id.Profile) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.nav_host_fragment_content_homepage_developer, new developer_profile());
                    ft.commit();
                    handled = true;
                } else if (id == R.id.Home) {
                    navController.navigate(R.id.nav_home);
                    handled = true;
                } else if (id == R.id.findjob) {
                    Intent i=new Intent(Homepage_developer.this, FindjobActivity.class);
                    startActivity(i);
                } else if (id == R.id.evaluate_project) {
                    // TODO: Handle evaluate_project manually or navigate to its destination.
                    handled = true;
                } else if (id == R.id.chat) {
                    Intent i=new Intent(Homepage_developer.this, Chat.class);
                    startActivity(i);

                } else if (id == R.id.ContactUs) {
                    // Use the updated Contactus fragment with a no-argument constructor.
                    Contactus contactusFragment = new Contactus();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment_content_homepage_developer, contactusFragment)
                            .commit();
                    handled = true;
                } else if (id==R.id.appLogOut) {
                    permanentLogout();
                }

                // Close the drawer after a selection is made.
                drawer.closeDrawers();
                if (handled) {
                    return true;
                }
                return NavigationUI.onNavDestinationSelected(item, navController)
                        || Homepage_developer.super.onOptionsItemSelected(item);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if present.
        getMenuInflater().inflate(R.menu.homepage_developer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homepage_developer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void permanentLogout() {
        FirebaseAuth.getInstance().signOut();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(new Intent(this, login.class));
        finish();
    }
    @Override
    protected void onDestroy() {
        Intent serviceIntent = new Intent(this, FirebaseMessageListenerService.class);
        stopService(serviceIntent);
        Intent serviceIntent1 = new Intent(this, FirebaseConnectionService.class);
        stopService(serviceIntent1);
        Intent serviceIntent2 = new Intent(this, FirebaseProposalListenerService.class);
        stopService(serviceIntent2);


        super.onDestroy();
    }
}