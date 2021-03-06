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
import fr.boudonpierre.myyoutube.interfaces.YouTubeService;
import fr.boudonpierre.myyoutube.interfaces.ListFragmentCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pierre BOUDON.
 */
public class ListFragment extends Fragment {

    /* BINDED VIEWS */
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    /* VARIABLES */
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    public static View.OnClickListener myOnClickListener;
    public static Boolean isReloaded = false;

    public static int currentUser;
    public static ArrayList<Video> starredVideos;
    public static ArrayList<Video> listVideos;

    public static Fragment newInstance(int currentUser) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putInt("currentUser", currentUser);
        fragment.setArguments(args);

        return fragment;
    }

    public static ListFragmentCallback callback;
    public static Boolean tabletMode = false;

    /* ONCREATE / ONCREATEVIEW / ONVIEWCREATED */
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

        /* Know which json to load */
        if (isReloaded)
            fillRecycler(YouTubeService.ENDPOINT2, true);
        else
            fillRecycler(YouTubeService.ENDPOINT, false);

        /* -- SwipeRefreshLayout - Refresh System -- */
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isReloaded)
                    fillRecycler(YouTubeService.ENDPOINT, false);
                else
                    fillRecycler(YouTubeService.ENDPOINT2, true);

                isReloaded = !isReloaded;
            }
        });
    }

    /* OVERRIDED METHODS */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ListFragmentCallback){
            callback = (ListFragmentCallback) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        starredVideos = FavorisPreferences.retrieveStarredVideos(getContext(), currentUser);
    }

    /* PERSONNAL METHODS */
    private void fillRecycler(String endpoint, Boolean isForReload) {
        // Retrofit Builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YouTubeService apiService = retrofit.create(YouTubeService.class);

        // Set which json to call
        Call<ArrayList<Video>> call;
        if (isForReload) {
            call = apiService.getVideosReloaded();
        } else {
            call = apiService.getVideos();
        }

        // Add Callback to call queue
        call.enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(Call<ArrayList<Video>> call, Response<ArrayList<Video>> response) {
                int statusCode = response.code();
                ArrayList<Video> videos = response.body();

                if (videos != null)
                    showVideos(videos, statusCode);
                else
                    showError(getResources().getString(R.string.no_videos));

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<Video>> call, Throwable t) {
                showError(String.valueOf(t));
                showError(getResources().getString(R.string.connection_error));
            }
        });
    }

    private void showVideos(ArrayList<Video> videos, int statusCode) {
        if (statusCode == 200) {
            listVideos = videos;

            adapter = new CustomAdapter(videos, getContext(), currentUser);
            recyclerView.setAdapter(adapter);
        } else
            Toast.makeText(getContext(), R.string.error_code + String.valueOf(statusCode), Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    /* PRIVATE STATIC CLASSES */
    public static class MyOnClickListenerForMain implements View.OnClickListener {

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
            Video currentVideo = listVideos.get(recyclerView.getChildAdapterPosition(v));

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
