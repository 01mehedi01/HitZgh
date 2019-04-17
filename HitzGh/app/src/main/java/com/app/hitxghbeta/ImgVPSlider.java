package com.app.hitxghbeta;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.util.List;


/**
 * Created by Ankit on 27-03-2017.
 */

public class ImgVPSlider extends PagerAdapter {


    private List<String> imgs;
    private String img;
    private LayoutInflater inflater;
    private Context context;
    private Boolean fullSizeImg;
    PhotoViewAttacher mAttacher;

    public ImgVPSlider(Context context,List<String>imgs,Boolean fullSizeImg) {
        this.context = context;
        this.imgs = imgs;
        this.fullSizeImg = fullSizeImg;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.image_item, view, false);

        assert imageLayout != null;
        final PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.sliderImageView);

        if(fullSizeImg&&imgs.get(position).contains("?"))
            img = imgs.get(position).substring(0,imgs.get(position).indexOf("?"));
        else
            img = imgs.get(position);
        Log.e("IMAGE URL",img);
        Glide.with(context)
                .load(img)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .error(R.drawable.category_gradient)
                .crossFade()
                .into(photoView);
        view.addView(imageLayout, 0);
        mAttacher = new PhotoViewAttacher(photoView);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
