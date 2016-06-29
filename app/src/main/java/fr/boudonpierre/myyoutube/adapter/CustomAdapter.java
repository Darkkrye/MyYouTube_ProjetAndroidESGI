package fr.boudonpierre.myyoutube.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.boudonpierre.myyoutube.activities.AppActivity;
import fr.boudonpierre.myyoutube.classes.MyVariables;
import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.classes.Video;
import fr.boudonpierre.myyoutube.fragments.FavoritesFragment;
import fr.boudonpierre.myyoutube.fragments.ListFragment;
import fr.boudonpierre.myyoutube.interfaces.ItemTouchHelperAdapter;

/**
 * Created by Pierre BOUDON.
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
        AppActivity activity = (AppActivity) parent.getContext();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag("listFragment");
        if (fragment != null && fragment.isVisible()) {
            view.setOnClickListener(ListFragment.myOnClickListener);
        } else {
            view.setOnClickListener(FavoritesFragment.myOnClickListener);
        }

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        // Fill with data
        holder.tvTitle.setText(videos.get(listPosition).getName());
        holder.tvDescription.setText(videos.get(listPosition).getDescription());
        Picasso.with(holder.itemView.getContext()).load(videos.get(listPosition).getImageUrl()).resize(475, 325).into(holder.imageViewIcon);
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
        notifyItemRangeRemoved(position, MyVariables.starredVideos.size());

        // Save the modified array
        MyVariables.saveStarredVideos(context);

        // Check if the starred videos array is empty to show message and change to AppActivity
        if (MyVariables.starredVideos == null || MyVariables.starredVideos.isEmpty()) {
            Toast.makeText(context, R.string.no_more_favorites, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, AppActivity.class);
            context.startActivity(intent);
        }
    }


    /* PUBLIC STATIC CLASSES */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        /* BINDED VIEWS */
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.imageView) ImageView imageViewIcon;

        /* CONSTRUCTOR */
        public MyViewHolder(View itemView) {
            super(itemView);

            /* Bind Views */
            ButterKnife.bind(this, itemView);
        }
    }
}
