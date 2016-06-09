package fr.boudonpierre.myyoutube;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by OpenFieldMacMini on 06/06/2016.
 */
public interface YouTubeService {

    public final static String ENDPOINT = "https://raw.githubusercontent.com/florent37/MyYoutube/master/";
    public final static String ENDPOINT2 = "https://gist.githubusercontent.com/Darkkrye/f175e06dff09bddb73664257040faad5/raw/1a68fc64fd402258bba609e06140b340797f9699/";

    @GET("myyoutube.json")
    Call<ArrayList<Video>> getVideos();

    @GET("reloadedJSONMyYouTube.json")
    Call<ArrayList<Video>> getVideosReloaded();
}
