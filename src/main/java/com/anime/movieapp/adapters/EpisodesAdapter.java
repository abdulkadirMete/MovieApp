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

import java.util.List;

import io.realm.RealmList;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>{
    private Context mContext;
    private int epSize;
    private List<String> epNameList;
    private OnEpisodeListener onEpListener;
    private RealmList<Integer> epHistoryList;
    private long mLastClickTime = 0;

    public EpisodesAdapter(Context mContext,List<String> epNameList,OnEpisodeListener onEpListener,RealmList<Integer> epHistoryList) {
        this.mContext = mContext;
        this.epNameList=epNameList ;
        this.onEpListener = onEpListener;
        this.epHistoryList = epHistoryList;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_episode,parent,false);
        return new EpisodeViewHolder(v,onEpListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        holder.tv_episodes.setText(epNameList.get(position));
        if(epHistoryList.contains(position)){
            holder.img_watch_state.setImageResource(R.drawable.ic_watch_eye_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return epNameList.size();
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_episodes;
        OnEpisodeListener onEpListener;
        ImageView img_watch_state;


        public EpisodeViewHolder(@NonNull View itemView,OnEpisodeListener onEpListener) {
            super(itemView);
            tv_episodes = itemView.findViewById(R.id.tv_episodes);
            img_watch_state = itemView.findViewById(R.id.img_watch_state);
            itemView.setOnClickListener(this);
            this.onEpListener = onEpListener;
        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            img_watch_state.setImageResource(R.drawable.ic_watch_eye_24dp);
            onEpListener.onEpClick(getAdapterPosition());
       }

    }

    public interface OnEpisodeListener{
       void onEpClick(int position);
   }

}
