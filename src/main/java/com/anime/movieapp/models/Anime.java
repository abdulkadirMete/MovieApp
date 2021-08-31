package com.anime.movieapp.models;

import java.io.Serializable;

public class Anime implements Serializable {
    private String coverUrl;
    private String imageUrl;
    private String name;
    private String describtion;
    private String pageUrl;
    private String key;

    public Anime(String coverUrl, String imageUrl, String name, String describtion, String url) {
        this.coverUrl = coverUrl;
        this.imageUrl = imageUrl;
        this.name = name;
        this.describtion = describtion;
        this.pageUrl = pageUrl;
    }

    public Anime() {
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}



