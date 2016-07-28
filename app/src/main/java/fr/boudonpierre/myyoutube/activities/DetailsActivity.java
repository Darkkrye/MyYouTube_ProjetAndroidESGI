package fr.boudonpierre.myyoutube.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.boudonpierre.myyoutube.classes.FavorisPreferences;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.classes.Video;

/**
 * Created by Pierre BOUDON.
 */
public class DetailsActivity extends AppCompatActivity {

    /* BINDED VIEWS */
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.image) ImageView topImage;
    @BindView(R.id.title) TextView videoName;
    @BindView(R.id.description) TextView videoDescription;

    @BindView(R.id.layoutFavoriteButton) LinearLayout layoutFavoriteButton;
    @BindView(R.id.layoutViewVideoButton) LinearLayout layoutViewVideoButton;

    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.textStar) TextView textStar;
    @BindView(R.id.shareFAB) FloatingActionButton shareFAB;

    /* VARIABLES */
    private CollapsingToolbarLayout collapsingToolbarLayout;

    Video video;

    public static int currentUser;
    public static ArrayList<Video> starredVideos;

    /* ONCREATE */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /* Bind Views */
        ButterKnife.bind(this);

        currentUser = getIntent().getIntExtra("currentUser", 0);
        video = getIntent().getParcelableExtra("currentVideo");
        starredVideos = getIntent().getParcelableArrayListExtra("starredVideos");

        /* Transition */
        initActivityTransitions();
        ViewCompat.setTransitionName(appBarLayout, "extraImage");
        supportPostponeEnterTransition();

        /* -- Navigation Drawer -- */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = collapsingToolbar;
        collapsingToolbarLayout.setTitle(video.getName());
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        /* Fill with data */
        videoName.setText(video.getName());
        videoDescription.setText(video.getDescription());
        Picasso.with(this).load(video.getImageUrl()).into(topImage, new Callback() {
            @Override public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) topImage.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override public void onError() {
                Toast.makeText(getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        });

        /* Set Favorite state */
        for (int i = 0; i < starredVideos.size(); i++) {
            if (this.video.getId().equals(starredVideos.get(i).getId())) {
                this.imageStar.setImageResource(R.drawable.star);
                this.textStar.setText(R.string.delete_favorites);

                break;
            }
        }
    }

    /* OVERRIDED METHODS */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Return to previous Activity
        finish();
        return super.onOptionsItemSelected(menuItem);
    }

    /* PERSONNAL METHODS */
    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Create slide transition
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        // Get colors
        int primaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        int primary = ContextCompat.getColor(this, R.color.colorPrimary);

        // Set colors
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int color = palette.getMutedColor(primary);
            color = (color & 0x00FFFFFF) | 0x90000000;
            window.setStatusBarColor(color);
        }

        // Update UI
        updateBackground(shareFAB, palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        // Get colors
        int lightVibrantColor = palette.getLightVibrantColor(ContextCompat.getColor(this,R.color.dark_white));
        int vibrantColor = palette.getVibrantColor(ContextCompat.getColor(this,R.color.colorAccent));

        // Set colors
        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }



    /* -- Butterknife - OnClickListeners -- */
    @OnClick(R.id.layoutFavoriteButton)
    public void onLayoutFavoriteButtonClick() {
        // Get state of video
        Boolean isAlreadyStarred = false;
        int index = 0;

        for (int i = 0; i < starredVideos.size(); i++) {
            if (video.getId().equals(starredVideos.get(i).getId())) {
                isAlreadyStarred = true;
                index = i;
                break;
            }
        }

        // Set / Unset favorite video
        if (!isAlreadyStarred) {
            starredVideos.add(video);
            FavorisPreferences.saveStarredVideos(getApplicationContext(), starredVideos, currentUser);

            Toast.makeText(getApplicationContext(), R.string.added_favorites, Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.star);
            textStar.setText(R.string.delete_favorites);
        } else {
            starredVideos.remove(index);
            FavorisPreferences.saveStarredVideos(getApplicationContext(), starredVideos, currentUser);

            Toast.makeText(getApplicationContext(), R.string.deleted_favorites, Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.empty_star);
            textStar.setText(R.string.add_favorites);
        }
    }

    @OnClick(R.id.layoutViewVideoButton)
    public void onLayoutViewVideoButtonClick() {
        // Open video URL in web browser
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoUrl()));
        startActivity(browse);
    }

    @OnClick(R.id.shareFAB)
    public void onShareFABClick() {
        // Share video URL in other application
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = getResources().getString(R.string.watch_video) + " \n" + video.getVideoUrl();

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.share_video);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);


        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_video_via)));
    }
}
