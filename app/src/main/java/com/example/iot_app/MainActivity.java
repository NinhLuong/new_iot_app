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
    String gusername;
    String username;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        String json = prefs.getString("rooms", "[]");

/*        SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.jsonToRooms(json);*/
//        viewModel.jsonToRoomsArea(json1);
    }
    public String getGusername() {
        return this.gusername;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        replaceFragment(new HomeFragment());

        setUpViewPager();
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.web) {
                replaceFragment(new WebFragment());
            } else if (id == R.id.account) {
                replaceFragment(new AccountFragment());
            }
            return true;
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        /*SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        String json = viewModel.roomsToJson();*/
//        String json1 = viewModel.roomsAreaToJson();

//        editor.putString("rooms", json);
//        editor.putString("devices", json1);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
    }
    private void replaceFragment(Fragment fragment){
        if (fragment instanceof AccountFragment) {
            ((AccountFragment) fragment).setUsername(username);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}