package com.warships;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.warships.db.DbHelper;
import com.warships.model.User;

import layout.MainScreenFragment;
import layout.SettingsFragment;
import layout.StatisticsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int fragmentsCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Fragment newFragment = new MainScreenFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.content_main, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DbHelper dbHelper = new DbHelper(getBaseContext());
        User.reloadCurrentUserData(dbHelper.getReadableDatabase());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) && fragmentsCounter != 0) {
            drawer.closeDrawer(GravityCompat.START);
            fragmentsCounter--;
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    public void onResume() {
        if (User.getCurrentUser() == null) {
            Intent loginActivity = new Intent(this, LoginActivity.class);
            startActivity(loginActivity);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment newFragment = null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_settings:
                // Create new fragment and transaction
                newFragment = new SettingsFragment();
                break;
            case R.id.nav_main:
                newFragment = new MainScreenFragment();
                break;
            case R.id.nav_statistics:
                newFragment = new StatisticsFragment();
                break;
            default:
                break;
        }
        transaction.replace(R.id.content_main, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentsCounter++;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
