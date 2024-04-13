package com.example.iot_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.iot_app.account_page.AccountFragment;
import com.example.iot_app.account_page.ChangePW;
import com.example.iot_app.databinding.ActivityMainBinding;
import com.example.iot_app.home_page.HomeFragment;
import com.example.iot_app.webview_page.WebFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    // This is a binding object for the activity to use in order to interact with the views defined in the XML layout files.
    String gusername;
    String username;

    @Override
    protected void onStart() {
        super.onStart();
        // Retrieve and hold the contents of the preferences file 'MyApp', returning a SharedPreferences through which you can retrieve and modify its values.
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        // Retrieve a String value from the preferences.
        String json = prefs.getString("rooms", "[]");
//        String json1 = prefs.getString("devices", "[]");

        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        // Get a ViewModelProvider, which retains ViewModels while a scope (an activity or a fragment) is alive. Then get the SharedViewModel class from the ViewModelProvider.
        viewModel.jsonToRooms(json);
//        viewModel.jsonToRoomsArea(json1);
        // Convert the JSON string back to rooms using a method in the ViewModel.
    }
    public String getGusername() {
        return this.gusername;
    }

    // ActivityMainBinding cho phép người dùng truy cập và tương tác với các view trong file XML
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityMainBinding.inflate nạp các layout của file XML vào binding để dể gọi đến và sữ dụng
        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        Inflate the XML layout and return an instance of ActivityMainBinding.
        setContentView(binding.getRoot());
        // Set the root view for this activity.

        Intent intent = new Intent(this, StatusService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        username = getIntent().getStringExtra("username");
        gusername = username;

        Intent i = new Intent(MainActivity.this, ChangePW.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        i.putExtras(bundle);
        /*HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        homeFragment.setArguments(bundle);
*/
// Replace the current fragment with a new instance of HomeFragment.
        replaceFragment(new HomeFragment());

        setUpViewPager();
        // Set up the ViewPager with the sections adapter.
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        // Find the bottom navigation view from the layout.

// Set a listener that will be notified when a bottom navigation item is selected.
        binding.bottomNav.setOnItemSelectedListener(item -> {
//lấy id của item đc nhận vào
            int id = item.getItemId();
            // Get the ID for the selected item.
            // If home item is selected
            if (id == R.id.home) {
                // Replace current fragment with new instance of HomeFragment.
                replaceFragment(new HomeFragment());
            } else if (id == R.id.web) {
                // Replace current fragment with new instance of WebFragment.
                replaceFragment(new WebFragment());
            } else if (id == R.id.account) {
                // Replace current fragment with new instance of AccountFragment.
                replaceFragment(new AccountFragment());
            }
            return true;
        });

    }

    @Override
    protected void onStop() {
        // Called when you are no longer visible to the user.
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        // Retrieve and hold the contents of the preferences file 'MyApp', returning a SharedPreferences through which you can retrieve and modify its values.
        SharedPreferences.Editor editor = prefs.edit();
        // Create a new Editor for these preferences, through which you can make modifications to the data in the preferences and atomically commit those changes back to the SharedPreferences object.
        // Get a ViewModelProvider, which retains ViewModels while a scope (an activity or a fragment) is alive. Then get the SharedViewModel class from the ViewModelProvider.
        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        // Convert rooms to JSON string using a method in ViewModel.
        String json = viewModel.roomsToJson();
//        String json1 = viewModel.roomsAreaToJson();

        editor.putString("rooms", json);
//        editor.putString("devices", json1);
        // Set a String value in the preferences editor, to be written back once commit() or apply() are called.
        editor.apply();
        // Commit your preferences changes back from this Editor to the SharedPreferences object it is editing. This atomically performs the requested modifications, replacing whatever is currently in the SharedPreferences.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This hook is called whenever an item in your options menu is selected. The default implementation simply returns false to have the normal processing happen (calling the item's Runnable or sending a message to its Handler as appropriate).
        if (item.getItemId() == android.R.id.home) {
            // If home button in action bar is clicked
            // Pop all back stack states up to one that has given identifier. This includes popping all fragment states and their associated state from all back stacks until one whose identifier matches given one.
            getSupportFragmentManager().popBackStack();
            // Return true because we've handled this click event.
            return true;
        }
        // Call superclass method if we haven't handled this click event.
        return super.onOptionsItemSelected(item);
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        // Create an instance of ViewPagerAdapter.

    }
    // Method to replace current fragment with new one.
    private void replaceFragment(Fragment fragment){
        if (fragment instanceof AccountFragment) {
            ((AccountFragment) fragment).setUsername(username);
        }
// Get an instance of FragmentManager for interacting with fragments associated with this activity.
        FragmentManager fragmentManager = getSupportFragmentManager();
// Start a series of edit operations on the Fragments associated with this FragmentManager.t
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
// Replace an existing fragment that was added to a container. This is essentially the same as calling remove(Fragment)
//for all currently added fragments that were added with the same containerViewId and then add(int, Fragment, String) with the same arguments given here.
        fragmentTransaction.replace(R.id.frameLayout, fragment);
// Schedules a commit of this transaction. The commit does not happen immediately
// it will be scheduled as work on the main thread to be done the next time that thread is ready.
        fragmentTransaction.commit();
    }
}