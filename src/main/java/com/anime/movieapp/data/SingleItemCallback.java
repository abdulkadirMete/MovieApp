package com.anime.movieapp.data;

import com.anime.movieapp.models.Anime;

import java.util.List;

public interface SingleItemCallback {
    void onSingleCallback(List<Anime> mAnimeList);
}
