package fr.boudonpierre.myyoutube.activities;

import android.content.Context;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.boudonpierre.myyoutube.classes.MyVariables;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.classes.Video;
import fr.boudonpierre.myyoutube.fragments.DetailsFragment;

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
    Video video = MyVariables.currentVideo;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        ViewCompat.setTransitionName(appBarLayout, "extraImage");
        supportPostponeEnterTransition();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String itemTitle = MyVariables.currentVideo.getName();
        collapsingToolbarLayout = collapsingToolbar;
        collapsingToolbarLayout.setTitle(itemTitle);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        Picasso.with(this).load(MyVariables.currentVideo.getImageUrl()).into(topImage, new Callback() {
            @Override public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) topImage.getDrawable()).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });
            }

            @Override public void onError() {

            }
        });

        videoName.setText(itemTitle);
        videoDescription.setText(MyVariables.currentVideo.getDescription());

        /* Set Favorite state */
        for (int i = 0; i < MyVariables.starredVideos.size(); i++) {
            if (this.video.getId().equals(MyVariables.starredVideos.get(i).getId())) {
                this.imageStar.setImageResource(R.drawable.star);
                this.textStar.setText("Supprimer des favoris");

                break;
            }
        }
    }

    @Override public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        int primaryDark = ContextCompat.getColor(this,R.color.colorPrimaryDark);
        int primary = ContextCompat.getColor(this,R.color.colorPrimary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        updateBackground(shareFAB, palette);
        supportStartPostponedEnterTransition();
    }

    private void updateBackground(FloatingActionButton fab, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(ContextCompat.getColor(this,R.color.color_white));
        int vibrantColor = palette.getVibrantColor(ContextCompat.getColor(this,R.color.colorAccent));

        fab.setRippleColor(lightVibrantColor);
        fab.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    /* OVERRIDED METHODS */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Return to previous Activity
        finish();
        return super.onOptionsItemSelected(menuItem);
    }

    /* -- Butterknife - OnClickListeners -- */
    @OnClick(R.id.layoutFavoriteButton)
    public void onLayoutFavoriteButtonClick() {
        // Get state of video
        Boolean isAlreadyStarred = false;
        int index = 0;

        for (int i = 0; i < MyVariables.starredVideos.size(); i++) {
            if (video.getId().equals(MyVariables.starredVideos.get(i).getId())) {
                isAlreadyStarred = true;
                index = i;
                break;
            }
        }

        // Set / Unset favorite video
        if (!isAlreadyStarred) {
            MyVariables.starredVideos.add(video);
            MyVariables.saveStarredVideos(getApplicationContext());

            Toast.makeText(getApplicationContext(), "Ajouté aux favoris", Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.star);
            textStar.setText("Supprimer des favoris");
        } else {
            MyVariables.starredVideos.remove(index);
            MyVariables.saveStarredVideos(getApplicationContext());

            Toast.makeText(getApplicationContext(), "Favoris supprimé", Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.empty_star);
            textStar.setText("Ajouter aux favoris");
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

        String shareBody = "Regardez cette video : \n" + video.getVideoUrl();

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Partagez cette vidéo");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, "Partager cette vidéo via"));
    }














    /* BINDED VIEWS */
    /*@BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.detailsImage) ImageView detailsImage;
    @BindView(R.id.videoName) TextView videoName;
    @BindView(R.id.videoDescription) TextView videoDescription;

    @BindView(R.id.layoutFavoriteButton) LinearLayout layoutFavoriteButton;
    @BindView(R.id.layoutViewVideoButton) LinearLayout layoutViewVideoButton;

    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.textStar) TextView textStar;*/

    /* VARIABLES */
    //Video video = MyVariables.currentVideo;

    /* ONCREATE */
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /* Bind Views */
        //ButterKnife.bind(this);

        /* -- Navigation Drawer -- */
        /*setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Get Screen Width */
        /*DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        /* Fill with data */
        /*Picasso.with(this).load(this.video.getImageUrl()).resize(width, 1000).into(this.detailsImage);
        this.videoName.setText(this.video.getName());
        this.videoDescription.setText(this.video.getDescription());

        /* Set Favorite state */
        /*for (int i = 0; i < MyVariables.starredVideos.size(); i++) {
            if (this.video.getId().equals(MyVariables.starredVideos.get(i).getId())) {
                this.imageStar.setImageResource(R.drawable.star);
                this.textStar.setText("Supprimer des favoris");

                break;
            }
        }
    }*/


    /* OVERRIDED METHODS */
    /*@Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Return to previous Activity
        finish();
        return super.onOptionsItemSelected(menuItem);
    }*/


    /* -- Butterknife - OnClickListeners -- */
    /*@OnClick(R.id.layoutFavoriteButton)
    public void onLayoutFavoriteButtonClick() {
        // Get state of video
        Boolean isAlreadyStarred = false;
        int index = 0;

        for (int i = 0; i < MyVariables.starredVideos.size(); i++) {
            if (video.getId().equals(MyVariables.starredVideos.get(i).getId())) {
                isAlreadyStarred = true;
                index = i;
                break;
            }
        }

        // Set / Unset favorite video
        if (!isAlreadyStarred) {
            MyVariables.starredVideos.add(video);
            MyVariables.saveStarredVideos(getApplicationContext());

            Toast.makeText(getApplicationContext(), "Ajouté aux favoris", Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.star);
            textStar.setText("Supprimer des favoris");
        } else {
            MyVariables.starredVideos.remove(index);
            MyVariables.saveStarredVideos(getApplicationContext());

            Toast.makeText(getApplicationContext(), "Favoris supprimé", Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.empty_star);
            textStar.setText("Ajouter aux favoris");
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

        String shareBody = "Regardez cette video : \n" + video.getVideoUrl();

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Partagez cette vidéo");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, "Partager cette vidéo via"));
    }*/
}
