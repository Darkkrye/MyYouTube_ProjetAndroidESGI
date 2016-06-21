package fr.boudonpierre.myyoutube.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.classes.MyVariables;
import fr.boudonpierre.myyoutube.classes.Video;

/**
 * Created by OpenFieldMacMini on 21/06/2016.
 */
public class DetailsFragment extends Fragment {

    /* Binded View */
    @BindView(R.id.detailsImage) ImageView detailsImage;
    @BindView(R.id.videoName) TextView videoName;
    @BindView(R.id.videoDescription) TextView videoDescription;

    @BindView(R.id.layoutFavoriteButton) LinearLayout layoutFavoriteButton;
    @BindView(R.id.layoutViewVideoButton) LinearLayout layoutViewVideoButton;

    @BindView(R.id.imageStar) ImageView imageStar;
    @BindView(R.id.textStar) TextView textStar;

    /* Variables */
    Video video = MyVariables.currentVideo;

    public static Fragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        /* Get Screen Width */
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        /* Fill with datas */
        Picasso.with(getContext()).load(this.video.getImageUrl()).resize(width, 1000).into(this.detailsImage);
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
            MyVariables.saveStarredVideos(getContext());

            Toast.makeText(getContext(), "Ajouté aux favoris", Toast.LENGTH_SHORT).show();

            imageStar.setImageResource(R.drawable.star);
            textStar.setText("Supprimer des favoris");
        } else {
            MyVariables.starredVideos.remove(index);
            MyVariables.saveStarredVideos(getContext());

            Toast.makeText(getContext(), "Favoris supprimé", Toast.LENGTH_SHORT).show();

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
}
