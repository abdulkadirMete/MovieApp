package com.anime.movieapp.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.anime.movieapp.R;
import com.anime.movieapp.models.Anime;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SliderPagerAdapter extends PagerAdapter implements View.OnClickListener {
    private Context mContext;
    private List<Anime> mList;
    private OnSliderListener onSliderClick;
    private long mLastClickTime = 0;

    public SliderPagerAdapter(Context mContext, List<Anime> mList,OnSliderListener onSliderClick) {
        this.mContext = mContext;
        this.mList = mList;
        this.onSliderClick = onSliderClick;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Anime animeCurrent = mList.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.item_slide,null);
        ImageView slideImg = slideLayout.findViewById(R.id.slider_img);
//        TextView slideText = slideLayout.findViewById(R.id.slider_title);
        RequestOptions options = new RequestOptions().fitCenter();
        Glide.with(mContext).load(animeCurrent.getCoverUrl()).apply(options).into(slideImg);
//        slideText.setText(animeCurrent.getName());
        container.addView(slideLayout);
        slideLayout.setOnClickListener(this);
        return slideLayout;
    }

    @Override
    public int getCount() {
        return mList.size();
    }



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        onSliderClick.onSliderClick();
    }

    public interface OnSliderListener{
        void onSliderClick();
    }

}
