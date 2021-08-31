package com.anime.movieapp.data;


import androidx.annotation.NonNull;

import com.anime.movieapp.models.Anime;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseConnection {
    private DatabaseReference mDatabaseRef;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

//    public void getAnimeWithQuery(final FirebaseCallback firebaseCallback, String searchText) {
//        final List<Anime> mAnimeList = new ArrayList<>();
//        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
//        Query firebaseSearchQuery = mDatabaseRef.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Anime anime = postSnapshot.getValue(Anime.class);
//                    anime.setKey(postSnapshot.getKey());
//                    mAnimeList.add(anime);
//                }
//                firebaseCallback.onCallback(mAnimeList);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//
//        };
//        firebaseSearchQuery.addValueEventListener(valueEventListener);
//    }
    public void getData(final SingleItemCallback singleItemCallback, String key) {
        final List<Anime> mAnimeList = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(key);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Anime anime = dataSnapshot.getValue(Anime.class);
                if(anime != null){
                    anime.setKey(dataSnapshot.getKey());
                }
                mAnimeList.add(anime);
                singleItemCallback.onSingleCallback(mAnimeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        };
        mDatabaseRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void getAllSliders(final SliderCallBack sliderCallBack) {
        final List<Anime> sliderList = new ArrayList<>();
        mFirestore.collection("sliders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sliderList.add(document.toObject(Anime.class));
                            }
                            sliderCallBack.onCallbackSlider(sliderList);

                        } else {

                        }
                    }
                });
    }

    public void getRecent(final RecentCallback recentCallback){
        final List<Anime> mAnimeList = new ArrayList<>();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        Query firebaseSearchQuery = mDatabaseRef.limitToLast(5);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Anime anime = postSnapshot.getValue(Anime.class);
                    anime.setKey(postSnapshot.getKey());
                    mAnimeList.add(anime);
                }
                recentCallback.onRecentCallback(mAnimeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
        firebaseSearchQuery.addValueEventListener(valueEventListener);
    }

    public void getSuggestion(final SuggestionsCallback suggestionsCallback) {
        final List<Anime> sliderList = new ArrayList<>();
        mFirestore.collection("populars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sliderList.add(document.toObject(Anime.class));
                            }
                            suggestionsCallback.onSuggestionCallBack(sliderList);

                        } else {
                        }
                    }
                });
    }

    public void getDataAll(final FirebaseCallback firebaseCallback) {
        final List<Anime> mAnimeList = new ArrayList<>();
        mDatabaseRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference("uploads");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Anime anime = ds.getValue(Anime.class);
                    if (anime != null) {
                        anime.setKey(ds.getKey());
                    }
                    mAnimeList.add(anime);
                }
                firebaseCallback.onCallback(mAnimeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        };
        mDatabaseRef.orderByChild("name").addListenerForSingleValueEvent(valueEventListener);
    }

    public void getKeyAll(final SizeCallback sizeCallback) {
        final int[] firebaseSize = new int[1];
        mDatabaseRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference("size");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseSize[0] = dataSnapshot.child("-M0kp9wNmwVe29y9VBBv").getValue(Integer.class);
                sizeCallback.onSizeCallback(firebaseSize[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        };
        mDatabaseRef.addListenerForSingleValueEvent(valueEventListener);
    }
}

