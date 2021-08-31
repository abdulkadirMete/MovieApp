package com.anime.movieapp.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.movieapp.R;
import com.anime.movieapp.models.Anime;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class AnimeListAdapter extends RecyclerView.Adapter<AnimeListAdapter.ListHolder> implements Filterable {
    private Context mContex;
    private List<Anime> mAnimeList;
    private List<Anime> mAnimeFull;
    private OnListAnimeItemListener mListener;
    private long mLastClickTime = 0;

    public AnimeListAdapter(Context mContex, List<Anime> mAnimeList, OnListAnimeItemListener mListener) {
        this.mContex = mContex;
        this.mAnimeList = mAnimeList;
        this.mListener = mListener;
        mAnimeFull = new ArrayList<>(mAnimeList);
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContex).inflate(R.layout.item_anime_card,parent,false);
        return  new AnimeListAdapter.ListHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHolder holder, int position) {
        holder.tv_name.setText(mAnimeList.get(position).getName());
        Anime animeCurrent = mAnimeList.get(position);
        holder.tv_name.setText(animeCurrent.getName());
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(mContex).load(animeCurrent.getImageUrl()).apply(options).into(holder.img_anime);
    }

    @Override
    public int getItemCount() {
        return mAnimeList.size();
    }

    @Override
    public Filter getFilter() {

        return accessFilter;
    }

    private Filter accessFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Anime> filteredAnime = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredAnime.addAll(mAnimeFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Anime anime : mAnimeFull) {
                    if (anime.getName().toLowerCase().contains(filterPattern)) {
                        filteredAnime.add(anime);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredAnime;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mAnimeList.clear();
            mAnimeList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name;
        ImageView img_anime;
        OnListAnimeItemListener mListener;

        public ListHolder(@NonNull View itemView,OnListAnimeItemListener mListener) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_anime = itemView.findViewById(R.id.img_anime);
            this.mListener = mListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            mListener.onListAnimeClick(getAdapterPosition());
        }
    }

    public interface OnListAnimeItemListener{
        void onListAnimeClick(int position);
    }
}
