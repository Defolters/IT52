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
public class PastFragment extends Fragment {
    public View view;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView listEvents;
    private View errorView;
    private View noInternetView;

    public PastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_past, container, false);

        errorView = view.findViewById(R.id.empty_list);
        noInternetView = view.findViewById(R.id.no_internet_list);

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Util.loadEvents(true, getActivity(), listEvents, Util.getEventsService(), swipeRefreshLayout, errorView, noInternetView, false);
            }
        });

        listEvents = view.findViewById(R.id.recycler_list_events); // WILL IT WORK?
        listEvents.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listEvents.setLayoutManager(layoutManager);

        Util.loadEvents(false, getActivity(), listEvents, Util.getEventsService(), swipeRefreshLayout, errorView, noInternetView, false);

        return view;
    }

}
