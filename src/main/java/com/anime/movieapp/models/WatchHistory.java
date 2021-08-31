package com.anime.movieapp.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WatchHistory extends RealmObject {
    @PrimaryKey
    private String key;

    private RealmList<Integer> epList;

    public WatchHistory(String key, RealmList<Integer> epList) {
        this.key = key;
        this.epList = epList;
    }

    public WatchHistory() {
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RealmList<Integer> getEpList() {
        return epList;
    }

    public void setEpList(RealmList<Integer> epList) {
        this.epList = epList;
    }

}
