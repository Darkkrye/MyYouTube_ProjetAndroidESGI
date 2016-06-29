package fr.boudonpierre.myyoutube.classes;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import fr.boudonpierre.myyoutube.R;
import fr.boudonpierre.myyoutube.widgets.FavoritesWidget;

/**
 * Created by Pierre BOUDON.
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
        Gson gson = new Gson();
        String json = gson.toJson(MyVariables.starredVideos);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit().putString(MyVariables.SPTAG + currentUser, json).commit();

        updateWidget(context);
    }

    public static void retrieveStarredVideos(Context context) {
        // Retrieve array of starred videos
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(MyVariables.SPTAG + currentUser, null);
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
        sharedPrefs.edit().putInt("currentUser", MyVariables.currentUser).commit();
    }

    public static void retrieveCurrentUser(Context context) {
        // Retrieve current user in header
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        MyVariables.currentUser = sharedPrefs.getInt("currentUser", 0);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, FavoritesWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, FavoritesWidget.appWidgetsIDs);
        context.sendBroadcast(intent);
    }
}
