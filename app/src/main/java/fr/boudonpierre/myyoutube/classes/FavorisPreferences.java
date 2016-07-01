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
 * Created by OpenFieldMacMini on 01/07/2016.
 */
public class FavorisPreferences {
    /* TAG */
    public static final String SPTAG = "starredVideo";


    /* USERS INFORMATION */
    public static final String[] usernames = {"Dexter MORGAN", "Odile DERAY", "Brenda MONTGOMERY", "Knights Who Say NI !"};
    public static final String[] emails = {"dexmorgan@ClickOn.me", "oderay@ClickOn.me", "bbbbrendaaa@ClickOn.me", "shrubbery@ClickOn.me"};
    public static final int[] profileImages = {R.drawable.t_profile, R.drawable.oderay, R.drawable.bmontgomery, R.drawable.ni};
    public static final int[] backgroundImages = {R.drawable.t_background_poly, R.drawable.background1, R.drawable.background2, R.drawable.background3};

    /* SAVE & RETRIEVE METHODS */
    public static void saveStarredVideos(Context context, ArrayList<Video> videos, int currentUser) {
        // Save array of starred videos
        Gson gson = new Gson();
        String json = gson.toJson(videos);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit().putString(SPTAG + currentUser, json).commit();

        updateWidget(context);
    }

    public static ArrayList<Video> retrieveStarredVideos(Context context, int currentUser) {
        // Retrieve array of starred videos
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(SPTAG + currentUser, null);
        Type type = new TypeToken<ArrayList<Video>>() {}.getType();
        ArrayList<Video> starredVideos = gson.fromJson(json, type);

        // Be sure to not have a null array
        if (starredVideos == null) {
            starredVideos = new ArrayList<Video>();
        }

        return starredVideos;
    }

    public static void saveCurrentUser(Context context, int currentUser) {
        // Save current user in header
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit().putInt("currentUser", currentUser).commit();
    }

    public static int retrieveCurrentUser(Context context) {
        // Retrieve current user in header
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getInt("currentUser", 0);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context, FavoritesWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, FavoritesWidget.appWidgetsIDs);
        context.sendBroadcast(intent);
    }
}
