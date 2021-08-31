package com.anime.movieapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anime.movieapp.R;

public class CominicationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cominication);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out);
    }
}
