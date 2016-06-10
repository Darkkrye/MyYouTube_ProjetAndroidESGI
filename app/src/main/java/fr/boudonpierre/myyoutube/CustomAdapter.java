package fr.boudonpierre.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by OpenFieldMacMini on 06/06/2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<Video> videos;
    Context context;

    @Override
    public Boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(MyVariables.starredVideos, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(MyVariables.starredVideos, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

        MyVariables.saveStarredVideos(context);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        MyVariables.starredVideos.remove(position);
        notifyItemRemoved(position);

        MyVariables.saveStarredVideos(context);

        if (MyVariables.starredVideos == null || MyVariables.starredVideos.isEmpty()) {
            Toast.makeText(context, "Plus aucun favoris !", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fromDrawer", true);
            context.startActivity(intent);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public CustomAdapter(ArrayList<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

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

        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;

        textViewName.setText(videos.get(listPosition).getName());
        textViewVersion.setText(videos.get(listPosition).getDescription());
        Picasso.with(holder.itemView.getContext()).load(videos.get(listPosition).getImageUrl()).resize(475, 325).into(imageView);

        //animate(holder);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }
}
