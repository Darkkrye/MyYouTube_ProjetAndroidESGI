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

    @GET("myyoutube.json")
    Call<ArrayList<Video>> getVideos();
}
