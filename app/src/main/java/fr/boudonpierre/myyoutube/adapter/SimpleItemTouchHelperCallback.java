package fr.boudonpierre.myyoutube.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import fr.boudonpierre.myyoutube.interfaces.ItemTouchHelperAdapter;

/**
 * Created by Pierre BOUDON.
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    /* VARIABLES */
    private final ItemTouchHelperAdapter mAdapter;


    /* CONSTRUCTOR */
    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }


    /* OVERRIDED METHODS */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // Call back when item has been moved
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Call back when item has been swiped
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

}
