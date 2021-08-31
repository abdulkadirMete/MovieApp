package com.anime.movieapp.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.movieapp.R;
import com.anime.movieapp.models.Anime;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.MyViewHolder> {

    private Context mContext ;
    private List<Anime> mAnimeList;
    private AnimeItemClickListener animeClickListener;
    private int rvNo;
    private long mLastClickTime = 0;

    public AnimeAdapter(Context mContext, List<Anime> mAnimeList, AnimeItemClickListener animeClickListener,int rvNo) {
        this.mContext = mContext;
        this.mAnimeList = mAnimeList;
        this.animeClickListener = animeClickListener;
        this.rvNo = rvNo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_anime_card,parent,false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.animeName.setText(mAnimeList.get(i).getName());
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(mContext).load(mAnimeList.get(i).getImageUrl()).apply(options).into(holder.animeImg);
    }

    @Override
    public int getItemCount() {
        return mAnimeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView animeName;
        private ImageView animeImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            animeName = itemView.findViewById(R.id.tv_name);
            animeImg = itemView.findViewById(R.id.img_anime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    animeClickListener.onAnimeClick(getAdapterPosition(),rvNo);
                }
            });

        }
    }
}
