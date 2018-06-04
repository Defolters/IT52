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
    private RecyclerView listEvents;
    private RecyclerView.LayoutManager layoutManager;
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

        ComingFragment comingFragment = new ComingFragment();
        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.frame_layout, comingFragment,"FragmentName");
        fragmentTransaction1.commit();


        //Init cache
        Paper.init(this);

        //Init Service
        eventsService = Util.getEventsService();

        //Init View
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadEvents(true);
            }
        });

        listEvents = (RecyclerView) findViewById(R.id.list_events);
        listEvents.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listEvents.setLayoutManager(layoutManager);

//        dialog = new SpotsDialog(this);





        loadEvents(false);
    }

    private void loadEvents(boolean isRefreshed) {
        if (!isRefreshed) {

            String cache = Paper.book().read("cache");
            if (cache != null && !cache.isEmpty() && !cache.equals("null")) // If have cache
            {
                Events events = new Gson().fromJson(cache, Events.class); // Convert cache from Json to Object
                adapter = new EventsListAdapter(getBaseContext(), events); //TODO: sort cards and pass them to different layouts
                adapter.notifyDataSetChanged();
                listEvents.setAdapter(adapter);
            } else // If not have cache
            {
//            dialog.show();
                //Fetch new data
                eventsService.getEvents().enqueue(new Callback<Events>() {
                    @Override
                    public void onResponse(Call<Events> call, Response<Events> response) {
                        adapter = new EventsListAdapter(getBaseContext(), response.body());
                        adapter.notifyDataSetChanged();
                        listEvents.setAdapter(adapter);

                        //Save to cache
                        Paper.book().write("cache", new Gson().toJson(response.body()));

//                    dialog.dismiss();

                    }

                    @Override
                    public void onFailure(Call<Events> call, Throwable t) {

                    }
                });
            }
        } else // If from Swipe to Refresh
        {

            swipeLayout.setRefreshing(true);
            //Fetch new data
            eventsService.getEvents().enqueue(new Callback<Events>() {
                @Override
                public void onResponse(Call<Events> call, Response<Events> response) {
                    adapter = new EventsListAdapter(getBaseContext(), response.body());
                    adapter.notifyDataSetChanged();
                    listEvents.setAdapter(adapter);

                    //Save to cache
                    Paper.book().write("cache", new Gson().toJson(response.body()));

                    //Dismiss refresh progressring
                    swipeLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<Events> call, Throwable t) {

                }
            });

        }
    }
}

