package com.example.project3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.project3.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ContainerActivity extends AppCompatActivity {
   BottomNavigationView bottomNavigationView ;
   HomeFragment homeFragment =  new HomeFragment() ;
   SettingFragment settingFragment = new SettingFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_item_icon:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,homeFragment).commit();
                        return true ;
                    case R.id.setting_item_icon:
                        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,settingFragment).commit();
                        return true ;

                }
                return false ;

            }
        });

    }
}