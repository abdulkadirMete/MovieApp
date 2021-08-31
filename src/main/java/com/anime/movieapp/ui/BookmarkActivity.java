package com.anime.movieapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class BookmarkActivity extends AppCompatActivity implements SearchAdapter.OnAnimeListener, SingleItemCallback {
    private RecyclerView rv_bookmark;
    private GridLayoutManager layoutManager;
    private SearchAdapter adapter;
    private SQliteHelper db;
    private List<Anime> bookmarkList;
    private ProgressBar pb_bookmark;
    public static final int Request_Code = 1;
    private Anime anime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmark);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        initTb();
        init();
        initSpace();
        updateRv();

    }

    private void initTb() {
        getSupportActionBar().setTitle("Kaydedilenler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        pb_bookmark = findViewById(R.id.pb_bookmark);
        rv_bookmark = findViewById(R.id.rv_bookmark);
        rv_bookmark.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_bookmark.setLayoutManager(layoutManager);
        db = new SQliteHelper(this);
        bookmarkList = new ArrayList<>();
    }

    private void initSpace() {
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(20);
        rv_bookmark.addItemDecoration(itemDecoration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateRv() {
        bookmarkList.clear();
        bookmarkList = db.getAllAnimes();
        adapter = new SearchAdapter(this,bookmarkList,this);
        rv_bookmark.setAdapter(adapter);
    }

    @Override
    public void onAnimeClickListener(int position) {
        pb_bookmark.setVisibility(View.VISIBLE);
        anime = bookmarkList.get(position);
        FirebaseConnection con = new FirebaseConnection();
        con.getData(this,anime.getKey());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request_Code && resultCode == RESULT_OK &&
                data.getBooleanExtra("bookmarkState",false)) {
                updateRv();
        }
    }

    @Override
    public void onSingleCallback(List<Anime> mAnimeList) {
        Anime currentAnime = mAnimeList.get(0);
        if(currentAnime != null) {
            Intent intent = new Intent(this, AnimeDetailActivity.class);
            intent.putExtra("anime", currentAnime);
            startActivityForResult(intent, Request_Code);
            pb_bookmark.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(this,R.string.bookmark,Toast.LENGTH_LONG).show();
            db.animeDelete(anime.getKey());
            pb_bookmark.setVisibility(View.GONE);
            updateRv();
        }
    }
}
