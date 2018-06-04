package io.github.defolters.it52;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComingFragment extends Fragment {
    public View view;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listEvents;

    public ComingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_coming, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_coming);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Util.loadEvents(true, getActivity(), listEvents, Util.getEventsService(), swipeRefreshLayout, true);
            }
        });

        listEvents = (RecyclerView)view.findViewById(R.id.list_events_coming); // WILL IT WORK?
        listEvents.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listEvents.setLayoutManager(layoutManager);

        Util.loadEvents(false, getActivity(), listEvents, Util.getEventsService(), swipeRefreshLayout, true);

        return view;
    }
}
