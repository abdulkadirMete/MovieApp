package com.anime.movieapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.anime.movieapp.R;
import com.anime.movieapp.adapters.AnimeAdapter;
import com.anime.movieapp.adapters.AnimeItemClickListener;
import com.anime.movieapp.adapters.SliderPagerAdapter;
import com.anime.movieapp.data.FirebaseCallback;
import com.anime.movieapp.data.FirebaseConnection;
import com.anime.movieapp.data.RecentCallback;
import com.anime.movieapp.data.SQliteHelper;
import com.anime.movieapp.data.SizeCallback;
import com.anime.movieapp.data.SliderCallBack;
import com.anime.movieapp.data.SuggestionsCallback;
import com.anime.movieapp.models.Anime;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity implements AnimeItemClickListener, SliderCallBack
        , SliderPagerAdapter.OnSliderListener, FirebaseCallback
        ,SuggestionsCallback, SizeCallback, RecentCallback {

    private ViewPager sliderPager;
    private TabLayout indicator;
    private RecyclerView rv_recent;
    private RecyclerView rv_suggestions;
    private Context context = this;
    private FirebaseConnection con = new FirebaseConnection();
    private List<Anime> sliderList;
    private List<Anime> recentList;
    private List<Anime> suggestionList;
    private SliderPagerAdapter.OnSliderListener onSliderClick = this;
    private SliderPagerAdapter adapter;
    private ProgressBar pb_main;
    private SQliteHelper db;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isOnline()){
            setContentView(R.layout.activity_home);
            initTb();
            pb_main = findViewById(R.id.pb_main);
            initSlider();
            initRv();
            checkDataSync();
        }else {
            setContentView(R.layout.no_internet_connection);
        }
    }

    private void checkDataSync() {
        db = new SQliteHelper(context);
        db.getWritableDatabase();
        con.getKeyAll(this);
    }

    @Override
    public void onSizeCallback(int size) {
        if(size != db.rowCount()) {
            db.deleteAllForList();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getText(R.string.pleaseWaitSync));
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);


            con.getDataAll(this);
        }
    }

    @Override
    public void onCallback(List<Anime> mAnimeList) {
        if(mAnimeList.size()>0){
            db.animeAddForList(mAnimeList);
            progressDialog.dismiss();
        }else{
            Toast.makeText(context,getText(R.string.errorNotKnow),Toast.LENGTH_SHORT).show();
        }
    }

    private void initTb() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void initSlider() {
        sliderPager = findViewById(R.id.slider_pager);
        indicator = findViewById(R.id.indicator);
        sliderList = new ArrayList<>();
        con.getAllSliders(this);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new HomeActivity.SliderTimer(), 4000, 6000);
        indicator.setupWithViewPager(sliderPager, true);
    }

    @Override
    public void onSliderClick() {
        Anime currentAnime = sliderList.get(sliderPager.getCurrentItem());
        Intent intent = new Intent(context, AnimeDetailActivity.class);
        intent.putExtra("anime",currentAnime);
        startActivity(intent);
    }
    @Override
    public void onCallbackSlider(List<Anime> mAnimeList) {
        sliderList.addAll(mAnimeList);
        adapter = new SliderPagerAdapter(context, sliderList,onSliderClick);
        sliderPager.setAdapter(adapter);

    }

    private void initRv() {
        rv_recent = findViewById(R.id.rv_recent);
        rv_suggestions = findViewById(R.id.rv_suggestions);
        con.getRecent(this);
        con.getSuggestion(this);
    }

    @Override
    public void onAnimeClick(int position,int rvNo) {
        Intent intent = new Intent(context, AnimeDetailActivity.class);
        if(rvNo==0){
            intent.putExtra("anime",recentList.get(position));
            startActivity(intent);
        }else{
            intent.putExtra("anime",suggestionList.get(position));
            startActivity(intent);
        }

    }

    @Override
    public void onSuggestionCallBack(List<Anime> mAnimeList) {
        suggestionList = new ArrayList<>();
        suggestionList.addAll(mAnimeList);
        AnimeAdapter animeAdapter = new AnimeAdapter(context, suggestionList, this,1);
        rv_suggestions.setAdapter(animeAdapter);
        rv_suggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onRecentCallback(List<Anime> mAnimeList) {
        recentList = new ArrayList<>();
        recentList.addAll(mAnimeList);
        AnimeAdapter animeAdapter = new AnimeAdapter(context, recentList, this,0);
        rv_recent.setAdapter(animeAdapter);
        rv_recent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pb_main.setVisibility(View.GONE);
    }

    class SliderTimer extends TimerTask {

        @Override
        public void run() {

            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderPager.getCurrentItem() < sliderList.size() - 1) {
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem() + 1);

                    } else
                        sliderPager.setCurrentItem(0);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem search_item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>0) {
                    Intent intent = new Intent(context, AnimeSearchActivity.class);
                    intent.putExtra("query",query);
                    startActivity(intent);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                Intent intent = new Intent(this,BookmarkActivity.class);
                startActivity(intent);
                break;

            case R.id.action_list:
                Intent intent2 = new Intent(this,AnimeListActivity.class);
                startActivity(intent2);
                break;

            case R.id.action_settings:
                Intent intent3 = new Intent(this,CominicationActivity.class);
                startActivity(intent3);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}