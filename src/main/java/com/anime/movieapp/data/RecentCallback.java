package com.anime.movieapp.data;

import com.anime.movieapp.models.Anime;

import java.util.List;

public interface RecentCallback {
    void onRecentCallback(List<Anime> mAnimeList);
}
