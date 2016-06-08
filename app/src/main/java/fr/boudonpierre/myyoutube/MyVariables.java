package fr.boudonpierre.myyoutube;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by OpenFieldMacMini on 08/06/2016.
 */
public class MyVariables {
    public static final String SPTAG = "starredVideo";
    public static ArrayList<Video> videos;
    public static ArrayList<Video> starredVideos;
    public static Video currentVideo;

    public static void saveStarredVideos(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(MyVariables.starredVideos);

        editor.putString(MyVariables.SPTAG, json);
        editor.commit();
    }

    public static void retrieveStarredVideos(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(MyVariables.SPTAG, null);
        Type type = new TypeToken<ArrayList<Video>>() {}.getType();
        MyVariables.starredVideos = gson.fromJson(json, type);

        if (MyVariables.starredVideos == null) {
            MyVariables.starredVideos = new ArrayList<Video>();
        }
    }
}
