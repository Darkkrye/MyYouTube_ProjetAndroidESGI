package fr.boudonpierre.myyoutube.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.activities.DetailsActivity;
import fr.boudonpierre.myyoutube.adapter.CustomAdapter;
import fr.boudonpierre.myyoutube.interfaces.ItemTouchHelperAdapter;
import fr.boudonpierre.myyoutube.adapter.SimpleItemTouchHelperCallback;
import fr.boudonpierre.myyoutube.classes.MyVariables;
import fr.boudonpierre.myyoutube.interfaces.ListFragmentCallback;

/**
 * Created by OpenFieldMacMini on 21/06/2016.
 */
public class FavoritesFragment extends Fragment {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    public static View.OnClickListener myOnClickListener;
    public static Boolean isReloaded = false;
    public static ListFragmentCallback callback;
    public static Boolean tabletMode = false;

    public static Fragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (getActivity().findViewById(R.id.detailsContentLayout) != null) {
            tabletMode = true;
        }

        /* -- RecyclerView - Set information -- */
        recyclerView = (RecyclerView) view.findViewById(R.id.videoRecycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        this.myOnClickListener = new MyOnClickListenerForMain(getContext());

        /* -- SwipeRefreshLayout - Refresh System -- */
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new CustomAdapter(MyVariables.starredVideos, getContext());
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Load the reloaded and good one starredVideo array
        if (MyVariables.starredVideos == null || MyVariables.starredVideos.isEmpty()) {
            Toast.makeText(getContext(), "Aucun favoris n'a été ajouté", Toast.LENGTH_SHORT).show();
        }

        adapter = new CustomAdapter(MyVariables.starredVideos, getContext());
        recyclerView.setAdapter(adapter);

        /* -- RecyclerView - Item Move -- */
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ListFragmentCallback){
            callback = (ListFragmentCallback) context;
        }
    }

    /* PERSONNAL METHODS */

    /* PRIVATE STATIC CLASSES */
    private static class MyOnClickListenerForMain implements View.OnClickListener {

        /* VARIABLES */
        private final Context context;

        /* CONSTRUCTOR */
        private MyOnClickListenerForMain(Context context) {
            this.context = context;
        }

        /* OVERRIDED METHODS */
        @Override
        public void onClick(View v) {
            // Set current video and change to DetailsActivity
            MyVariables.currentVideo = MyVariables.starredVideos.get(recyclerView.getChildAdapterPosition(v));

            if(tabletMode && callback != null){
                callback.onVideoClicked();
            } else {
                Intent i = new Intent(v.getContext(), DetailsActivity.class);
                v.getContext().startActivity(i);
            }
        }
    }
}
