package fr.boudonpierre.myyoutube;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    /* Binded View */
    ImageView detailsImage;
    TextView videoName;
    TextView videoDescription;
    LinearLayout layoutFavoriteButton;
    LinearLayout layoutViewVideoButton;

    ImageView imageStar;
    TextView textStar;

    /* Variables */
    Toolbar toolbar;
    Video video = MyVariables.currentVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /* -- Navigation Drawer -- */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Bind View */
        this.detailsImage = (ImageView) findViewById(R.id.detailsImage);
        this.videoName = (TextView) findViewById(R.id.videoName);
        this.videoDescription = (TextView) findViewById(R.id.videoDescription);
        this.layoutFavoriteButton = (LinearLayout) findViewById(R.id.layoutFavoriteButton);
        this.layoutViewVideoButton = (LinearLayout) findViewById(R.id.layoutViewVideoButton);

        this.imageStar = (ImageView) findViewById(R.id.imageStar);
        this.textStar = (TextView) findViewById(R.id.textStar);

        /* Get Screen Width */
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        /* Fill with datas */
        Picasso.with(this).load(this.video.getImageUrl()).resize(width, 1000).into(this.detailsImage);
        this.videoName.setText(this.video.getName());
        this.videoDescription.setText(this.video.getDescription());

        /* Set Favorite state */
        for (int i = 0; i < MyVariables.starredVideos.size(); i++) {
            if (this.video.getId().equals(MyVariables.starredVideos.get(i).getId())) {
                this.imageStar.setImageResource(R.drawable.star);
                this.textStar.setText("Supprimer des favoris");

                break;
            }
        }

        /* Set On Click Listeners */
        this.layoutFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isAlreadyStarred = false;
                int index = 0;

                for (int i = 0; i < MyVariables.starredVideos.size(); i++) {
                    if (video.getId().equals(MyVariables.starredVideos.get(i).getId())) {
                        isAlreadyStarred = true;
                        index = i;
                        break;
                    }
                }

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
        });
        this.layoutViewVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoUrl()));
                startActivity(browse);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        finish();
        return super.onOptionsItemSelected(menuItem);
    }
}
