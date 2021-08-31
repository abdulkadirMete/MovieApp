package com.anime.movieapp.data;

import android.content.Context;
import android.util.Log;

import com.anime.movieapp.models.WatchHistory;

import io.realm.Realm;
import io.realm.RealmList;

public class RealmHelper {

    private Realm realm;
    private String key;
    private Context context;
    private Integer epNum;
    public static final String TAG ="Lemur";

    public RealmHelper(Context context, Realm realm, String key, Integer epNum) {
        this.realm = realm;
        this.key = key;
        this.context = context;
        this.epNum = epNum;
    }

    public RealmHelper(Realm realm,String key) {
        this.realm = realm;
        this.key = key;
    }

    public void push(){
        WatchHistory object = realm.where(WatchHistory.class)
                .equalTo("key",key)
                .findFirst();
        if(object == null){
            saveData();
        } else {
            RealmList<Integer> mList = new RealmList<Integer>();
            mList.addAll(object.getEpList());

            if(!mList.contains(epNum)){
                mList.add(epNum);
            }
            WatchHistory people = new WatchHistory(key,mList);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(people);
            realm.commitTransaction();
            Log.i(TAG,"DEĞER KAYDEDİLDİ");
        }
    }

    public void saveData(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                WatchHistory people = bgRealm.createObject(WatchHistory.class,key);
                RealmList<Integer> episode = new RealmList<>();
                episode.add(epNum);
                people.setEpList(episode);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"KAYIT BAŞARILI");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.i(TAG,"KAYIT BAŞARISIZ");

            }
        });
    }

    public RealmList<Integer> getEpHistoryList(){
        WatchHistory history = realm.where(WatchHistory.class)
                .equalTo("key",key)
                .findFirst();
        if(history == null){
            RealmList<Integer> epHistoryList = new RealmList<Integer>();
            return  epHistoryList;
        } else {
            return history.getEpList();

        }
    }
}
