package com.app.hitxghbeta.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.app.hitxghbeta.Colors;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.PostListActivity;
import com.app.hitxghbeta.R;
import com.app.wplib.models.page.Page;
import com.app.wplib.models.post.Post;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

/**
 * Created by Anubhav on 17-08-2017.
 */

public class VerticalPostAdapter extends AbstractItem<VerticalPostAdapter, VerticalPostAdapter.ViewHolder> {

    private Context context;
    private int type,catId;
    private String title,cat,img;
    private Post post;

    public VerticalPostAdapter(Context context, Page page, int type) {
        this.context = context;
        this.title = page.getTitle().getRendered();
        if(page.getBetterFeaturedImage()!=null)
            this.img = page.getBetterFeaturedImage().getPostThumbnail();
        this.type = type;
    }

    public VerticalPostAdapter(Context context, Post post, int type) {
        this.context = context;
        this.title = post.getTitle().getRendered();
        this.type = type;
        this.post = post;
        if(Config.isPluginInstalled){
            if (post.getCategoryDetails()!=null&&post.getBetterFeaturedImage()!=null){
                if(post.getCategoryDetails().size()>0) {
                    this.catId = post.getCategoryDetails().get(0).getId();
                    this.cat = post.getCategoryDetails().get(0).getName();
                }
                this.img = post.getBetterFeaturedImage().getPostThumbnail();
            }
        }else {
            this.catId = post.getCategories().get(0);
            this.img = post.getFeat_media_url();
            this.cat = post.getCategories_string();
        }
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        switch (type){
            case 1: return R.id.verticalListTypeItem;
            case 2: return R.id.verticalListTypeItem2;
            default:
                return R.id.verticalListTypeItem;
        }
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        switch (type){
            case 1: return R.layout.vl_type_item;
            case 2: return R.layout.vl_type_item_2;
            default: return R.layout.vl_type_item;
        }

    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        Colors r = new Colors();
        viewHolder.categoryCardView.setCardBackgroundColor(ContextCompat.getColor(context,r.getRandomColor()));
        if(title!=null)
            viewHolder.title.setText(title);
        else
            viewHolder.category.setText("Undefined");
        if(cat!=null)
            viewHolder.category.setText(cat);
        else
            viewHolder.category.setVisibility(GONE);
        Log.e("Image","Url: "+img);
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
            return new ViewHolder(v,type);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView,categoryCardView;
        TextView title,category;
        ImageView imageView,iconView;
        public ViewHolder(View view,int x) {
            super(view);
            if(x==1){
                this.cardView = view.findViewById(R.id.vl_cardView);
                this.title = view.findViewById(R.id.vl_title);
                this.imageView = view.findViewById(R.id.vl_image);
                this.category = view.findViewById(R.id.vl_category);
                this.categoryCardView = view.findViewById(R.id.vl_category_cardview);
                this.iconView = view.findViewById(R.id.play_icon);
            }else if(x==2){
                this.cardView = view.findViewById(R.id.vl_cardview_2);
                this.title = view.findViewById(R.id.vl_textview_2);
                this.imageView = view.findViewById(R.id.vl_imgview_2);
                this.category = view.findViewById(R.id.vl_category_2);
                this.categoryCardView = view.findViewById(R.id.vl_category_cardview_2);
                this.iconView = view.findViewById(R.id.videoIcon);
            }else {
                this.cardView = view.findViewById(R.id.vl_cardView);
                this.title = view.findViewById(R.id.vl_title);
                this.imageView = view.findViewById(R.id.vl_image);
                this.category = view.findViewById(R.id.vl_category);
                this.categoryCardView = view.findViewById(R.id.vl_category_cardview);
                this.iconView = view.findViewById(R.id.play_icon);
            }
        }
    }

    public static class CategoryCardClickEvent extends ClickEventHook<VerticalPostAdapter> {
        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof VerticalPostAdapter.ViewHolder) {
                return ((ViewHolder) viewHolder).categoryCardView;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<VerticalPostAdapter> fastAdapter, VerticalPostAdapter item) {
            if(item.catId!=0) {
                Intent intent = new Intent(item.context, PostListActivity.class);
                intent.putExtra(PostListActivity.ARG_TITLE, item.cat);
                intent.putExtra(PostListActivity.ARG_CATEGORY, item.catId + "");
                intent.putExtra(PostListActivity.ARG_TYPE, item.type);
                intent.putExtra(PostListActivity.ARG_SHOW_IMG_URL, item.img);
                intent.putExtra(PostListActivity.ARG_SHOW_IMG, true);
                item.context.startActivity(intent);
            }else {
                Toasty.error(item.context,"Invalid category",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
