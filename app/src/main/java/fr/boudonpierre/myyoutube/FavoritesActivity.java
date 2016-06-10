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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class FavoritesActivity extends AppCompatActivity {

    /* Binded Views */
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.headerLayout) RelativeLayout headerLayoutDrawer;
    @BindView(R.id.homeLayout) LinearLayout homeLayoutDrawer;
    @BindView(R.id.favoritesLayout) LinearLayout favoritesLayoutDrawer;

    @BindView(R.id.profile_image) CircleImageView profileImage;
    @BindView(R.id.username) TextView tvusername;
    @BindView(R.id.email) TextView tvemail;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    /* Variables */
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    ActionBarDrawerToggle drawerToggle;

    static View.OnClickListener myOnClickListenerForFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Deactivate Animation */
        if (getIntent().getBooleanExtra("fromDrawer", false)) {
            getWindow().setWindowAnimations(0);
        }

        /* -- Get Binded Views -- */
        ButterKnife.bind(this);

        /* -- Navigation Drawer -- */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);

        /* -- Navigation Drawer - Set Properties -- */
        this.favoritesLayoutDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
        this.homeLayoutDrawer.setBackgroundColor(Color.TRANSPARENT);
        this.updateNavigationDrawerUI();

        myOnClickListenerForFavorite = new MyOnClickListenerForFavorite(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* -- SwipeRefreshLayout - Refresh System -- */
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = new CustomAdapter(MyVariables.starredVideos, getApplicationContext());
                recyclerView.setAdapter(adapter);

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MyVariables.starredVideos == null || MyVariables.starredVideos.isEmpty()) {
            Toast.makeText(this, "Aucun favoris n'a été ajouté", Toast.LENGTH_SHORT).show();
        }

        adapter = new CustomAdapter(MyVariables.starredVideos, this);
        recyclerView.setAdapter(adapter);

        /* -- RecyclerView - Item Move -- */
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
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

    /* -- Navigation Drawer - OnClickListeners -- */
    @OnClick(R.id.headerLayout)
    public void onHeaderLayoutDrawerClick() {
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
    @OnClick(R.id.homeLayout)
    public void onHomeLayoutDrawerClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);

        Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
        intent.putExtra("fromDrawer", true);
        startActivity(intent);
    }
    @OnClick(R.id.favoritesLayout)
    public void onFavoriteLayoutClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
    }
}
