package com.example.codebrains;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.codebrains.databinding.ActivityHomepageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Homepage extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomepageBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String profession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the binding layout
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        // Set up the toolbar
        setSupportActionBar(binding.appBarHomepage.toolbar);

        // Set up the floating action button
        binding.appBarHomepage.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Homepage.this, AIchatbot.class);
                startActivity(intent);

            }
        });

        // Set up drawer layout and navigation view
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configure the AppBar with top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        // Set up navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_homepage);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Handle navigation drawer item clicks manually where needed
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_contact_us) {
                    // Navigate to the Contact Us page using the no-argument constructor.
                    Contactus contactusFragment = new Contactus();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment_content_homepage, contactusFragment)
                            .commit();
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_home) {
                    // Navigate to the Home Fragment
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.nav_host_fragment_content_homepage, new HomeFragment());
                    ft.commit();
                    drawer.closeDrawers();
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Navigate to the Profile Fragment
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.nav_host_fragment_content_homepage, new ProfileFragment());
                    ft.commit();
                    drawer.closeDrawers();
                    return true;
                }else if (id == R.id.post) {
                    // Navigate to the Profile Fragment
                   Intent intent=new Intent(Homepage.this,JobpostingActivity.class);
                   startActivity(intent);

                }

                // Default behavior for other menu items
                return NavigationUI.onNavDestinationSelected(item, navController)
                        || Homepage.super.onOptionsItemSelected(item);
            }
        });
    }

    private void processProfession(String profession) {
        this.profession = profession;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if present.
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
        Intent intent=new Intent(Homepage.this , JobActivity.class);
        startActivity(intent);
    }
}
