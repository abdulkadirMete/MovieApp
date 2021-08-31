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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {
    private Context mContext;
    private List<Anime> mAnime;
    private List<Anime> mAnimeFull;
    private OnAnimeListener mOnAnimeListener;
    private long mLastClickTime = 0;


    public SearchAdapter(Context mContext, List<Anime> mAnime, OnAnimeListener mOnAnimeListener) {
        this.mContext = mContext;
        this.mAnime = mAnime;
        this.mOnAnimeListener = mOnAnimeListener;
        mAnimeFull = new ArrayList<>(mAnime);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_anime_card,parent,false);
        return new SearchViewHolder(v,mOnAnimeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Anime animeCurrent = mAnime.get(position);
        holder.search_anime_title.setText(animeCurrent.getName());
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(mContext).load(animeCurrent.getImageUrl()).apply(options).into(holder.search_anime_img);

    }

    @Override
    public int getItemCount() {
        return mAnime.size();
    }

    @Override
    public Filter getFilter() {
        return animeFilter;
    }

    private Filter animeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Anime> filteredAnime = new ArrayList<>();
            if(constraint == null || constraint.length()==0){
                filteredAnime.addAll(mAnimeFull);
            }else{
                String filterPattern =constraint.toString().toLowerCase().trim();

                for (Anime anime : mAnimeFull){
                    if(anime.getName().toLowerCase().contains(filterPattern)){
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
            mAnime.clear();
            mAnime.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView search_anime_title;
        ImageView search_anime_img;
        OnAnimeListener onAnimeListener;

        public SearchViewHolder(@NonNull View itemView,OnAnimeListener onAnimeListener) {
            super(itemView);
            search_anime_title = itemView.findViewById(R.id.tv_name);
            search_anime_img = itemView.findViewById(R.id.img_anime);
            this.onAnimeListener = onAnimeListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            onAnimeListener.onAnimeClickListener(getAdapterPosition());
        }
    }

    public interface OnAnimeListener{
        void onAnimeClickListener(int position);
    }

}
