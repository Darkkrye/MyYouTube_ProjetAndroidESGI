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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.activities.DetailsActivity;
import fr.boudonpierre.myyoutube.adapter.CustomAdapter;
import fr.boudonpierre.myyoutube.classes.FavorisPreferences;
import fr.boudonpierre.myyoutube.classes.Video;
import fr.boudonpierre.myyoutube.interfaces.ItemTouchHelperAdapter;
import fr.boudonpierre.myyoutube.adapter.SimpleItemTouchHelperCallback;
import fr.boudonpierre.myyoutube.interfaces.ListFragmentCallback;

/**
 * Created by Nicolas KERVOERN.
 */
public class FavoritesFragment extends Fragment {

    /* BINDED VIEWS */
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    /* VARIABLES */
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    public static View.OnClickListener myOnClickListener;
    public static Boolean isReloaded = false;
    public static ListFragmentCallback callback;
    public static Boolean tabletMode = false;

    public static int currentUser;
    public static ArrayList<Video> starredVideos;

    /* CONSTRUCTOR */
    public static Fragment newInstance(int currentUser) {
        FavoritesFragment fragment = new FavoritesFragment();

        Bundle args = new Bundle();
        args.putInt("currentUser", currentUser);
        fragment.setArguments(args);

        return fragment;
    }

    /* ONCREATE / ONCREATEVIEW / ONVIEWCREATED /  */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = getArguments().getInt("currentUser", 0);
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

        starredVideos = FavorisPreferences.retrieveStarredVideos(getContext(), currentUser);

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
                starredVideos = FavorisPreferences.retrieveStarredVideos(getContext(), currentUser);
                adapter = new CustomAdapter(starredVideos, getContext(), currentUser);
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /* OVERRIDED METHODS */
    @Override
    public void onResume() {
        super.onResume();

        starredVideos = FavorisPreferences.retrieveStarredVideos(getContext(), currentUser);

        // Load the reloaded and good one starredVideo array
        if (starredVideos == null || starredVideos.isEmpty()) {
            Toast.makeText(getContext(), R.string.no_favorites, Toast.LENGTH_SHORT).show();
        }

        adapter = new CustomAdapter(starredVideos, getContext(), currentUser);
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
            Video currentVideo = starredVideos.get(recyclerView.getChildAdapterPosition(v));

            if(tabletMode && callback != null){
                callback.onVideoClicked(currentUser, currentVideo, starredVideos);
            } else {
                Intent i = new Intent(v.getContext(), DetailsActivity.class);
                i.putExtra("currentUser", currentUser);
                i.putExtra("currentVideo", currentVideo);
                i.putParcelableArrayListExtra("starredVideos", starredVideos);
                v.getContext().startActivity(i);
            }
        }
    }
}
