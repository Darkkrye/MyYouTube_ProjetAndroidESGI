package fr.boudonpierre.myyoutube;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    static View.OnClickListener myOnClickListener;
    static ArrayList<Video> videos;
    static ArrayList<Video> starredVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fillRecycler();


    }


    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            Toast.makeText(v.getContext(), videos.get(selectedItemPosition).getName() + " selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Toast.makeText(this, "ADD selected", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void fillRecycler() {
        // Retrofit Builder
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YouTubeService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YouTubeService apiService = retrofit.create(YouTubeService.class);

        Call<ArrayList<Video>> call = apiService.getVideos();
        call.enqueue(new Callback<ArrayList<Video>>() {
            @Override
            public void onResponse(Call<ArrayList<Video>> call, Response<ArrayList<Video>> response) {
                int statusCode = response.code();
                ArrayList<Video> videos = response.body();

                if (videos != null)
                    showVideos(videos, statusCode);
                else
                    showError("There's no videos !");
            }

            @Override
            public void onFailure(Call<ArrayList<Video>> call, Throwable t) {
                showError(String.valueOf(t));
            }
        });
    }

    private void showVideos(ArrayList<Video> videos, int statusCode) {
        this.videos = videos;
        // Fill with data
        adapter = new CustomAdapter(videos);
        recyclerView.setAdapter(adapter);
    }

    private void showError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void saveStarredVideos() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(starredVideos);
        prefsEditor.putString("starredVideos", json);
        prefsEditor.commit();
    }

    private ArrayList<Video> retrieveStarredVideos() {

        return new ArrayList<Video>();
    }
}
