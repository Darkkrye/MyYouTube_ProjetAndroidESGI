package fr.boudonpierre.myyoutube.interfaces;

import java.util.ArrayList;

import fr.boudonpierre.myyoutube.classes.Video;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Nicolas KERVOERN.
 */
public interface YouTubeService {

    /* ENDPOINTS */
    public final static String ENDPOINT = "https://raw.githubusercontent.com/florent37/MyYoutube/master/";
    public final static String ENDPOINT2 = "https://gist.githubusercontent.com/Darkkrye/f175e06dff09bddb73664257040faad5/raw/1a68fc64fd402258bba609e06140b340797f9699/";

    /* REST CALL METHODS */
    @GET("myyoutube.json")
    Call<ArrayList<Video>> getVideos();

    @GET("reloadedJSONMyYouTube.json")
    Call<ArrayList<Video>> getVideosReloaded();
}
