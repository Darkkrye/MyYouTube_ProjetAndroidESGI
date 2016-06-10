package fr.boudonpierre.myyoutube.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import fr.boudonpierre.myyoutube.classes.MyVariables;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.classes.Video;
import fr.boudonpierre.myyoutube.activities.FavoritesActivity;
import fr.boudonpierre.myyoutube.activities.MainActivity;

/**
 * Created by Pierre BOUDON on 06/06/2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    /* VARIABLES */
    Context context;
    private ArrayList<Video> videos;


    /* CONSTRUCTOR */
    public CustomAdapter(ArrayList<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }


    /* ONCREATE / ONBIND / GETITEMCOUNT */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Create View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_layout, parent, false);

        // Set good On Click Listener
        if (parent.getContext() instanceof MainActivity) {
            view.setOnClickListener(MainActivity.myOnClickListenerForMain);
        } else {
            view.setOnClickListener(FavoritesActivity.myOnClickListenerForFavorite);
        }

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        // Fill with data
        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(videos.get(listPosition).getName());
        textViewVersion.setText(videos.get(listPosition).getDescription());
        Picasso.with(holder.itemView.getContext()).load(videos.get(listPosition).getImageUrl()).resize(475, 325).into(imageView);

        //animate(holder); // Test Animation (can be uncommented but result is not as handsome
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    /* OVERRIDED METHODS */
    @Override
    public Boolean onItemMove(int fromPosition, int toPosition) {
        // Move item from a position to another one
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(MyVariables.starredVideos, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(MyVariables.starredVideos, i, i - 1);
            }
        }

        // Apply the UI change
        notifyItemMoved(fromPosition, toPosition);

        // Save the modified array
        MyVariables.saveStarredVideos(context);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        // Remove item from the array and apply the UI change
        MyVariables.starredVideos.remove(position);
        notifyItemRemoved(position);

        // Save the modified array
        MyVariables.saveStarredVideos(context);

        // Check if the starred videos array is empty to show message and change to MainActivity
        if (MyVariables.starredVideos == null || MyVariables.starredVideos.isEmpty()) {
            Toast.makeText(context, "Plus aucun favoris !", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fromDrawer", true);
            context.startActivity(intent);
        }
    }


    /* PUBLIC STATIC CLASSES */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        /* VARIABLES */
        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;

        /* CONSTRUCTOR */
        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }


    /* PERSONNAL METHODS */
    // Test Animation (can be uncommented but result is not as handsome
    /*public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }*/
}
