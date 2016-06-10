package fr.boudonpierre.myyoutube;

/**
 * Created by OpenFieldMacMini on 10/06/2016.
 */
public interface ItemTouchHelperAdapter {

    Boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
