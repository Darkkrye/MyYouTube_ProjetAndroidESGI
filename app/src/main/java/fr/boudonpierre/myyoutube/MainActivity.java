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

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    /* Binded Views */
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;

    RelativeLayout headerLayoutDrawer;
    LinearLayout homeLayoutDrawer;
    LinearLayout favoritesLayoutDrawer;


    /* Variables */
    static View.OnClickListener myOnClickListenerForMain;
    //RecyclerView.ItemAnimator itemAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Deactivate Animation */
        if (getIntent().getBooleanExtra("fromDrawer", false)) {
            getWindow().setWindowAnimations(0);
        }

        /* -- Navigation Drawer -- */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);

        this.headerLayoutDrawer = (RelativeLayout) findViewById(R.id.headerLayout);
        this.homeLayoutDrawer = (LinearLayout) findViewById(R.id.homeLayout);
        this.favoritesLayoutDrawer = (LinearLayout) findViewById(R.id.favoritesLayout);

        /* -- Navigation Drawer - Set Properties -- */
        this.homeLayoutDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
        this.favoritesLayoutDrawer.setBackgroundColor(Color.TRANSPARENT);

        /* -- Navigation Drawer - OnClickListeners -- */
        this.headerLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Change user", Toast.LENGTH_SHORT).show();
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

        fillRecycler();

        /* Reload favorites */
        MyVariables.retrieveStarredVideos(this);

        /* Not as handsome
        // Animation
        this.itemAnimator = new DefaultItemAnimator();
        this.itemAnimator.setAddDuration(1000);
        this.itemAnimator.setRemoveDuration(1000);

        this.recyclerView.setItemAnimator(this.itemAnimator);*/
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

    private void fillRecycler() {
        // Retrofit Builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YouTubeService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YouTubeService apiService = retrofit.create(YouTubeService.class);

        Call<ArrayList<Video>> call = apiService.getVideos();
        call.enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(Call<ArrayList<Video>> call, Response<ArrayList<Video>> response) {
                int statusCode = response.code();
                ArrayList<Video> videos = response.body();

                if (videos != null)
                    showVideos(videos, statusCode);
                else
                    showError("There's no videos !");
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


}
