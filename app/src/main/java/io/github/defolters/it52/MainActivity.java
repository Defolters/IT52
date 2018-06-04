package io.github.defolters.it52;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import io.github.defolters.it52.Model.Events;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private boolean isFirstStart;
    private EventsService eventsService;

    private SwipeRefreshLayout swipeLayout;
    private EventsListAdapter adapter;
    //Context mcontext;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home); // TODO: load json from it52 and parse data
                    // start fragment
                    ComingFragment comingFragment = new ComingFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.frame_layout, comingFragment,"FragmentName");
                    fragmentTransaction1.commit();

                    return true;
                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
                    PastFragment pastFragment= new PastFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.frame_layout, pastFragment,"FragmentName");
                    fragmentTransaction2.commit();

                    return true;
                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
                    AboutFragment aboutFragment = new AboutFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.frame_layout, aboutFragment,"FragmentName");
                    fragmentTransaction3.commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ComingEvents fragment1 = new ComingEvents();
//        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction1.replace(R.id.frame_layout, fragment1, "Fragment One");  //create first framelayout with id fram in the activity where fragments will be displayed
//        fragmentTransaction1.commit();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (true) {//isFirstStart) {

                    //  Launch application introduction screen
                    Intent i = new Intent(MainActivity.this, IntroWizard.class);
                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

//        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Init cache
        Paper.init(this);

        //Init Service
        eventsService = Util.getEventsService();

        ComingFragment comingFragment = new ComingFragment();
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.frame_layout, comingFragment,"FragmentName");
        fragmentTransaction1.commit();
    }

}

