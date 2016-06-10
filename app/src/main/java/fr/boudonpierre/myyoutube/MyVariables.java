package fr.boudonpierre.myyoutube;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Pierre BOUDON on 08/06/2016.
 */
public class MyVariables {
    /* TAG */
    public static final String SPTAG = "starredVideo";


    /* USERS INFORMATION */
    public static final String[] usernames = {"Dexter MORGAN", "Odile DERAY", "Brenda MONTGOMERY", "Knights Who Say NI !"};
    public static final String[] emails = {"dexmorgan@ClickOn.me", "oderay@ClickOn.me", "bbbbrendaaa@ClickOn.me", "shrubbery@ClickOn.me"};
    public static final int[] profileImages = {R.drawable.t_profile, R.drawable.oderay, R.drawable.bmontgomery, R.drawable.ni};
    public static final int[] backgroundImages = {R.drawable.t_background_poly, R.drawable.background1, R.drawable.background2, R.drawable.background3};


    /* DATA */
    public static ArrayList<Video> videos;
    public static ArrayList<Video> starredVideos;
    public static Video currentVideo;
    public static int currentUser;


    /* SAVE & RETRIEVE METHODS */
    public static void saveStarredVideos(Context context) {
        // Save array of starred videos
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(MyVariables.starredVideos);

        editor.putString(MyVariables.SPTAG, json);
        editor.commit();
    }

    public static void retrieveStarredVideos(Context context) {
        // Retrieve array of starred videos
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(MyVariables.SPTAG, null);
        Type type = new TypeToken<ArrayList<Video>>() {}.getType();
        MyVariables.starredVideos = gson.fromJson(json, type);

        // Be sure to not have a null array
        if (MyVariables.starredVideos == null) {
            MyVariables.starredVideos = new ArrayList<Video>();
        }
    }

    public static void saveCurrentUser(Context context) {
        // Save current user in header
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        editor.putInt("currentUser", MyVariables.currentUser);
        editor.commit();
    }

    public static void retrieveCurrentUser(Context context) {
        // Retrieve current user in header
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        MyVariables.currentUser = sharedPrefs.getInt("currentUser", 0);
    }
}
