package com.anime.movieapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.movieapp.R;
import com.anime.movieapp.adapters.SearchAdapter;
import com.anime.movieapp.data.FirebaseConnection;
import com.anime.movieapp.data.SQliteHelper;
import com.anime.movieapp.data.SingleItemCallback;
import com.anime.movieapp.models.Anime;
import com.anime.movieapp.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AnimeSearchActivity extends AppCompatActivity implements SearchAdapter.OnAnimeListener, SingleItemCallback {
    private ArrayList<Anime> mAnimeList;
    private RecyclerView rv_search;
    private GridLayoutManager layoutManager;
    private Context context = this;
    private SearchAdapter adapter;
    private String searchText;
    private ProgressBar pb_search;
    private SQliteHelper db;
    private FirebaseConnection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_search);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        initTb();
        init();
        search();
    }

    private void initTb() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.searchResults);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
         mAnimeList = new ArrayList<>();
         db = new SQliteHelper(context);
         pb_search = findViewById(R.id.pb_search);
         rv_search = findViewById(R.id.rv_search);
         rv_search.setHasFixedSize(true);
         layoutManager = new GridLayoutManager(context,2);
         SpaceItemDecoration itemDecoration = new SpaceItemDecoration(20);
         rv_search.addItemDecoration(itemDecoration);
         rv_search.setLayoutManager(layoutManager);
         con = new FirebaseConnection();
    }

    private void search() {
        Intent intent = getIntent();
        searchText = intent.getStringExtra("query");
        mAnimeList.addAll(db.searchForList(searchText));
        adapter = new SearchAdapter(context,mAnimeList,this);
        rv_search.setAdapter(adapter);
        pb_search.setVisibility(View.GONE);
    }

    @Override
    public void onAnimeClickListener(int position) {
        Anime currentAnime = mAnimeList.get(position);
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
