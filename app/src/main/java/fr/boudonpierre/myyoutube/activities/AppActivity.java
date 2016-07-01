package fr.boudonpierre.myyoutube.activities;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.classes.FavorisPreferences;
import fr.boudonpierre.myyoutube.classes.Video;
import fr.boudonpierre.myyoutube.interfaces.ListFragmentCallback;
import fr.boudonpierre.myyoutube.classes.MyVariables;
import fr.boudonpierre.myyoutube.fragments.DetailsFragment;
import fr.boudonpierre.myyoutube.fragments.FavoritesFragment;
import fr.boudonpierre.myyoutube.fragments.ListFragment;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Pierre BOUDON.
 */
public class AppActivity extends AppCompatActivity implements ListFragmentCallback {

    /* BINDED VIEWS */
    @BindView(R.id.drawer) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.headerLayout) RelativeLayout headerLayoutDrawer;
    @BindView(R.id.homeLayout) LinearLayout homeLayoutDrawer;
    @BindView(R.id.favoritesLayout) LinearLayout favoritesLayoutDrawer;

    @BindView(R.id.profile_image) CircleImageView profileImage;
    @BindView(R.id.username) TextView tvusername;
    @BindView(R.id.email) TextView tvemail;

    /* VARIABLES */
    ActionBarDrawerToggle drawerToggle;

    public static int currentUser;
    public static ArrayList<Video> starredVideos;

    /* ONCREATE */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_app);

        /* Reload Shared Preferences */
        currentUser = FavorisPreferences.retrieveCurrentUser(this);
        starredVideos = FavorisPreferences.retrieveStarredVideos(this, currentUser);

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

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentLayout, ListFragment.newInstance(currentUser), "listFragment")
                .commit();
    }


    /* OVERRIDED METHODS */
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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /* PERSONAL METHODS */
    private void updateNavigationDrawerUI() {
        // Update textviews
        tvusername.setText(FavorisPreferences.usernames[currentUser]);
        tvemail.setText(FavorisPreferences.emails[currentUser]);

        // Update profile picture
        Picasso.with(getApplicationContext()).load(FavorisPreferences.profileImages[currentUser]).into(profileImage);

        // Update Header background of the Navigation Drawer
        Picasso.with(getApplicationContext())
                .load(FavorisPreferences.backgroundImages[currentUser])
                .into(new Target() {
                    @Override
                    @TargetApi(16)
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Load the image into the background header
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            headerLayoutDrawer.setBackgroundDrawable(new BitmapDrawable(bitmap));
                        } else {
                            headerLayoutDrawer.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(), String.valueOf(errorDrawable), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Do something when starts loading
                    }
                });
    }

    /* -- Butterknife - OnClickListeners -- */
    @OnClick(R.id.headerLayout)
    public void onHeaderLayoutDrawerClick() {
        ArrayList<Video> currentStarredVideos = FavorisPreferences.retrieveStarredVideos(getApplicationContext(), currentUser);
        FavorisPreferences.saveStarredVideos(getApplicationContext(), currentStarredVideos, currentUser);

        // Change current user and save it
        if (currentUser == FavorisPreferences.usernames.length - 1)
            currentUser = 0;
        else
            currentUser += 1;

        FavorisPreferences.saveCurrentUser(getApplicationContext(), currentUser);
        starredVideos = FavorisPreferences.retrieveStarredVideos(getApplicationContext(), currentUser);

        // Update UI
        updateNavigationDrawerUI();
    }

    @OnClick(R.id.homeLayout)
    public void onHomeLayoutDrawerClick() {
        homeLayoutDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));
        favoritesLayoutDrawer.setBackgroundColor(Color.TRANSPARENT);

        // Close Navigation Drawer and change to Home
        drawerLayout.closeDrawer(Gravity.LEFT);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentLayout, ListFragment.newInstance(currentUser), "listFragment")
                .commit();
    }

    @OnClick(R.id.favoritesLayout)
    public void onFavoriteLayoutClick() {
        homeLayoutDrawer.setBackgroundColor(Color.TRANSPARENT);
        favoritesLayoutDrawer.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_300));

        // Close Navigation Drawer and change to Favorites
        drawerLayout.closeDrawer(Gravity.LEFT);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contentLayout, FavoritesFragment.newInstance(currentUser), "favoritesListFragment")
                .addToBackStack(null)
                .commit();
    }

    /* FRAGMENT CALLBACKS */
    @Override
    public void onVideoClicked(int theCurrentUser, Video theCurrentVideo, ArrayList<Video> theStarredVideos) {
        if (findViewById(R.id.detailsContentLayout) != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailsContentLayout, DetailsFragment.newInstance(theCurrentUser, theCurrentVideo, theStarredVideos), "detailsFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }
}
