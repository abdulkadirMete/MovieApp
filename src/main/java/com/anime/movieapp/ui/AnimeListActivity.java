package com.anime.movieapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.movieapp.R;
import com.anime.movieapp.adapters.AnimeListAdapter;
import com.anime.movieapp.data.FirebaseConnection;
import com.anime.movieapp.data.SQliteHelper;
import com.anime.movieapp.data.SingleItemCallback;
import com.anime.movieapp.models.Anime;
import com.anime.movieapp.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AnimeListActivity extends AppCompatActivity implements AnimeListAdapter.OnListAnimeItemListener,SingleItemCallback{

    private RecyclerView rv_all;
    private LinearLayoutManager layoutManager;
    private AnimeListAdapter  adapter;
    private Context context = this;
    private List<Anime> mAllAnimes;
    private ProgressBar pb_list;
    private SQliteHelper db;
    FirebaseConnection con = new FirebaseConnection();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_list);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        initTb();
        init();
    }

    private void initTb() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Anime Listesi");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init(){
        pb_list = findViewById(R.id.pb_list);
        rv_all = findViewById(R.id.rv_all);
        layoutManager = new GridLayoutManager(context,2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_all.setLayoutManager(layoutManager);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(20);
        rv_all.addItemDecoration(itemDecoration);
        rv_all.setLayoutManager(layoutManager);
        db = new SQliteHelper(context);
        mAllAnimes = new ArrayList<>();
        mAllAnimes.addAll(db.getAllAnimesForList());
        adapter = new AnimeListAdapter(context,mAllAnimes,this);
        rv_all.setAdapter(adapter);
        pb_list.setVisibility(View.GONE);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_list_menu, menu);
        MenuItem search_item = menu.findItem(R.id.action_search_all);
        SearchView searchView = (SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onListAnimeClick(int position) {
        Anime currentAnime = mAllAnimes.get(position);
        con.getData(this,currentAnime.getKey());
    }

    @Override
    public void onSingleCallback(List<Anime> mAnimeList) {
        Anime currentAnime = mAnimeList.get(0);
        if(currentAnime != null) {
            Intent intent = new Intent(context, AnimeDetailActivity.class);
            intent.putExtra("anime",currentAnime);
            startActivity(intent);
        }else{
            Toast.makeText(context,getText(R.string.errorNotKnow),Toast.LENGTH_SHORT).show();
        }

    }
}

