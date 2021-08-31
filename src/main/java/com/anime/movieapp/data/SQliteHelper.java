package com.anime.movieapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.anime.movieapp.models.Anime;

import java.util.ArrayList;
import java.util.List;

public class SQliteHelper extends SQLiteOpenHelper {
    static final String COVER_URL="cover_url";
    static final String IMG_URL="img_url";
    static final String NAME="name";
    static final String PAGE_URL="page_url";
    static final String DESCRİBTİON="describtion";
    static final String KEY="key";

    static final String[] COLUMNS = {KEY,NAME,IMG_URL};

    //DB PROPERTIES
    static final String DB_NAME="animeDB";
    static final String TB_NAME_BOOKMARK="animes";
    static final String TB_NAME_LİST="listOfAnimes";
    static final int DB_VERSION=1;

    //CREATE TABLE STMT
    static final String CREATE_TB_BOOKMARK="CREATE TABLE \"animes\" (\n" +
            "\t\"key\"\tTEXT,\n" +
            "\t\"name\"\tTEXT,\n" +
            "\t\"img_url\"\tTEXT\n" +
            ")";

    static final String CREATE_TB_LİST="CREATE TABLE \"listOfAnimes\" (\n" +
            "\t\"img_url\"\tTEXT,\n" +
            "\t\"name\"\tTEXT,\n" +
            "\t\"key\"\tTEXT\n" +
            ")";

    public SQliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TB_BOOKMARK);
        db.execSQL(CREATE_TB_LİST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME_BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME_LİST);
        this.onCreate(db);
    }

    public void animeAdd(Anime anime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY, anime.getKey());
        values.put(IMG_URL,anime.getImageUrl());
        values.put(NAME,anime.getName());
        db.insert(TB_NAME_BOOKMARK, null, values);
        db.close();
    }

    public List<Anime> getAllAnimes() {
        List<Anime> animeList = new ArrayList<>();
        String query = "SELECT * FROM " + TB_NAME_BOOKMARK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Anime anime = null;
        if (cursor.moveToNext()) {
            do {
                anime = new Anime();
                anime.setKey(cursor.getString(0));
                anime.setName(cursor.getString(1));
                anime.setImageUrl(cursor.getString(2));

                animeList.add(anime);
            } while (cursor.moveToNext());


        }
        return animeList;
    }

    public void animeDelete(String keyD) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TB_NAME_BOOKMARK, KEY + " = ?", new String[]{keyD});
        db.close();
    }

    public Boolean isChecked(String key){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TB_NAME_BOOKMARK +" WHERE key=" + key;
        Cursor cursor = db.query(TB_NAME_BOOKMARK,COLUMNS,"key = ?", new String[]{key}, null, null
              , null);

        return (cursor.getCount()>0);
    }

    public void animeAddForList(List<Anime> animeList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i=0 ; i < animeList.size() ; i++){
            Anime anime = animeList.get(i);
            ContentValues values = new ContentValues();
            values.put(IMG_URL,anime.getImageUrl());
            values.put(NAME,anime.getName());
            values.put(KEY, anime.getKey());
            db.insert(TB_NAME_LİST, null, values);
        }
        db.close();
    }

    public List<Anime> getAllAnimesForList() {
        List<Anime> animeList = new ArrayList<>();
        String query = "SELECT * FROM " + TB_NAME_LİST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Anime anime = null;
        if (cursor.moveToNext()) {
            do {
                anime = new Anime();
                anime.setImageUrl(cursor.getString(0));
                anime.setName(cursor.getString(1));
                anime.setKey(cursor.getString(2));
                animeList.add(anime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return animeList;
    }

    public void deleteAllForList() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TB_NAME_LİST);
    }

    public List<Anime> searchForList(String searchText) {
        List<Anime> animeList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TB_NAME_LİST + " WHERE " + NAME + " LIKE '%" + searchText + "%'";
        Cursor cursor = db.rawQuery(sql, null);
        Anime anime = null;
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    anime = new Anime();
                    anime.setImageUrl(cursor.getString(0));
                    anime.setName(cursor.getString(1));
                    anime.setKey(cursor.getString(2));
                    animeList.add(anime);
                } while (cursor.moveToNext());

            }
        }
        cursor.close();
        return animeList;
    }

    public int rowCount(){
            SQLiteDatabase db = getReadableDatabase();
            long count = DatabaseUtils.queryNumEntries(db, TB_NAME_LİST);
            db.close();
            return (int) count;
    }
}