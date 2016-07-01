package fr.boudonpierre.myyoutube.interfaces;

import java.util.ArrayList;

import fr.boudonpierre.myyoutube.classes.Video;

/**
 * Created by Nicolas KERVOERN.
 */
public interface ListFragmentCallback {
    /* METHODS TO OVERRIDE */
    void onVideoClicked(int theCurrentUser, Video theCurrenVideo, ArrayList<Video> theStarredVideos);
}
