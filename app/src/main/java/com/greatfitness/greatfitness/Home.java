package com.greatfitness.greatfitness;



import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;

public class Home extends AppCompatActivity {

    //declaring a variable called fragment that will hold the fragment the user selected
    Fragment selectedItem;

    //method to check what page the user wants to go to and then changing the frame layout to the appropriate fragment
    //-------------------------------------------------------------------------------------------------------------
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedItem = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedItem = new HomeFragment();
                    break;
                case R.id.navigation_myactivity:
                    selectedItem = new MyActivityFragment();
                    break;
                case R.id.navigation_myprogress:
                    selectedItem = new MyProgressFragment();
                    break;
                case R.id.navigation_settings:
                    selectedItem = new SettingsFragment();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, selectedItem).commit();
            return true;
        }
    };
    //-------------------------------------------------------------------------------------------------------------

    //On create method to set the bottom navigation bar and setting the first fragment to the home fragment
    //----------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentLayout, new HomeFragment()).commit();
    }
    //----------------------------------------------------------------------------------------------------
}
