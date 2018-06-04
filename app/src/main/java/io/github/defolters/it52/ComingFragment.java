package io.github.defolters.it52;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComingFragment extends Fragment {
    public View view;
    public SwipeRefreshLayout swipeRefreshLayout;

    public ComingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_coming, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.getRootView().findViewById(R.id.swipe_refresh);
        return view;
    }

}
