package fr.boudonpierre.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

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
    static View.OnClickListener myOnClickListenerForFavorite;

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
        this.favoritesLayoutDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
        this.homeLayoutDrawer.setBackgroundColor(Color.TRANSPARENT);

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

                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                intent.putExtra("fromDrawer", true);
                startActivity(intent);
            }
        });
        this.favoritesLayoutDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });


        myOnClickListenerForFavorite = new MyOnClickListenerForFavorite(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (MyVariables.starredVideos != null && !MyVariables.starredVideos.isEmpty()) {
            adapter = new CustomAdapter(MyVariables.starredVideos, this);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Aucun favoris n'a été ajouté", Toast.LENGTH_SHORT).show();
        }
    }

    private static class MyOnClickListenerForFavorite implements View.OnClickListener {

        private final Context context;

        private MyOnClickListenerForFavorite(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Video selectedVideo = MyVariables.starredVideos.get(recyclerView.getChildAdapterPosition(v));
            //Toast.makeText(v.getContext(), selectedVideo.getName() + " starred", Toast.LENGTH_SHORT).show();

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
}
