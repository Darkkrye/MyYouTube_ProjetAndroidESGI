package fr.boudonpierre.myyoutube;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    /* Binded Views */
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.headerLayout) RelativeLayout headerLayoutDrawer;
    @BindView(R.id.homeLayout) LinearLayout homeLayoutDrawer;
    @BindView(R.id.favoritesLayout) LinearLayout favoritesLayoutDrawer;

    @BindView(R.id.profile_image) CircleImageView profileImage;
    @BindView(R.id.username) TextView tvusername;
    @BindView(R.id.email) TextView tvemail;

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;


    /* Variables */
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    ActionBarDrawerToggle drawerToggle;

    static View.OnClickListener myOnClickListenerForMain;
    //RecyclerView.ItemAnimator itemAnimator;

    public static Boolean isReloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Deactivate Animation */
        if (getIntent().getBooleanExtra("fromDrawer", false)) {
            getWindow().setWindowAnimations(0);
        }

        /* Reload Shared Preferences */
        MyVariables.retrieveStarredVideos(this);
        MyVariables.retrieveCurrentUser(this);

        /* -- Get Binded Views -- */
        ButterKnife.bind(this);

        /* -- Navigation Drawer -- */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);

        /* -- Navigation Drawer - Set Properties -- */
        this.homeLayoutDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
        this.favoritesLayoutDrawer.setBackgroundColor(Color.TRANSPARENT);
        this.updateNavigationDrawerUI();

        /* -- Navigation Drawer - OnClickListeners -- */
        this.headerLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "New user", Toast.LENGTH_SHORT).show();

                /* Change current user and save it */
                if (MyVariables.currentUser == MyVariables.usernames.length - 1) {
                    MyVariables.currentUser = 0;
                } else {
                    MyVariables.currentUser += 1;
                }
                MyVariables.saveCurrentUser(getApplicationContext());

                /* Update UI */
                updateNavigationDrawerUI();
            }
        });
        this.homeLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        this.favoritesLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);

                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                intent.putExtra("fromDrawer", true);
                startActivity(intent);
            }
        });


        myOnClickListenerForMain = new MyOnClickListenerForMain(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fillRecycler(YouTubeService.ENDPOINT, false);

        /* Not as handsome
        // Animation
        this.itemAnimator = new DefaultItemAnimator();
        this.itemAnimator.setAddDuration(1000);
        this.itemAnimator.setRemoveDuration(1000);

        this.recyclerView.setItemAnimator(this.itemAnimator);*/

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


    private static class MyOnClickListenerForMain implements View.OnClickListener {

        private final Context context;

        private MyOnClickListenerForMain(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Video selectedVideo = MyVariables.videos.get(recyclerView.getChildAdapterPosition(v));

            MyVariables.currentVideo = selectedVideo;

            Intent i = new Intent(v.getContext(), DetailsActivity.class);
            v.getContext().startActivity(i);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void fillRecycler(String endpoint, Boolean isForReload) {
        // Retrofit Builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YouTubeService apiService = retrofit.create(YouTubeService.class);


        Call<ArrayList<Video>> call;
        if (isForReload) {
            call = apiService.getVideosReloaded();
        } else {
            call = apiService.getVideos();
        }


        call.enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(Call<ArrayList<Video>> call, Response<ArrayList<Video>> response) {
                int statusCode = response.code();
                ArrayList<Video> videos = response.body();

                if (videos != null)
                    showVideos(videos, statusCode);
                else
                    showError("There's no videos !");

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<Video>> call, Throwable t) {
                showError(String.valueOf(t));
                showError("Un problème est survenu. Vérifiez votre connexion internet");
            }
        });
    }

    private void showVideos(ArrayList<Video> videos, int statusCode) {
        MyVariables.videos = videos;
        // Fill with data
        adapter = new CustomAdapter(videos, this);
        recyclerView.setAdapter(adapter);
    }

    private void showError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void updateNavigationDrawerUI() {
        tvusername.setText(MyVariables.usernames[MyVariables.currentUser]);
        tvemail.setText(MyVariables.emails[MyVariables.currentUser]);
        Picasso.with(getApplicationContext()).load(MyVariables.profileImages[MyVariables.currentUser]).into(profileImage);
        Picasso.with(getApplicationContext())
                .load(MyVariables.backgroundImages[MyVariables.currentUser])
                .into(new Target() {
                    @Override
                    @TargetApi(16)
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            headerLayoutDrawer.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        } else {
                            headerLayoutDrawer.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        // use error drawable if desired
                        Toast.makeText(getApplicationContext(), String.valueOf(errorDrawable), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // use placeholder drawable if desired
                    }
                });
    }
}
