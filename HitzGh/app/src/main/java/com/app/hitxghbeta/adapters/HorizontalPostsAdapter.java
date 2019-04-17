package com.app.hitxghbeta.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.R;
import com.app.wplib.models.post.Post;
import com.mikepenz.fastadapter.items.AbstractItem;


import java.util.List;

/**
 * Created by Anubhav on 15-08-2017.
 */

public class HorizontalPostsAdapter extends AbstractItem<HorizontalPostsAdapter, HorizontalPostsAdapter.ViewHolder> {

    private Context context;
    private boolean isCarousel = false;
    private Post post;
    private String title;
    private String img;
    public HorizontalPostsAdapter(Context context,Post post) {
        this.context = context;
        this.post = post;
        this.title = post.getTitle().getRendered();
        if(Config.isPluginInstalled&&post.getBetterFeaturedImage()!=null){
            this.img = post.getBetterFeaturedImage().getPostThumbnail();
        }else {
            this.img = null;
        }
    }
    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.horizontalPostsItem;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.horizontal_posts_item;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        //RandColors r = new RandColors();
        viewHolder.title.setText(title);
        Glide.with(context)
                .load(img)
                .placeholder(R.color.md_green_100)
                .error(R.drawable.no_image_placeholder)
                .into(viewHolder.imageView);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    //Init the viewHolder for this Item
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView title;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            this.cardView = view.findViewById(R.id.hpiCardView);
            this.title = view.findViewById(R.id.hpi_title);
            this.imageView = view.findViewById(R.id.hpi_image);
        }
    }
}