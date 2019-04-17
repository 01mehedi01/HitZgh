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
import com.app.wplib.models.category.Category;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by Anubhav on 17-08-2017.
 */

public class CategoryGridAdapter extends AbstractItem<CategoryGridAdapter, CategoryGridAdapter.ViewHolder> {

    private Context context;
    private Category category;
    private String count;
    private String title;
    private int color;

    public CategoryGridAdapter(Context context, Category category) {
        this.context = context;
        this.category = category;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
            return R.id.categoryItem;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
            return R.layout.category_item;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
            //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        /*Colors r = new Colors();
        color = r.getRandomColor();
        viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, color));*/
        try {
            String imageUrl = category.getCmb2().getWordroidFields().getCategoryImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .error(R.drawable.no_image_placeholder)
                        .into(viewHolder.imageView);
            } else {
                Glide.with(context)
                        .load(Config.defaultMediaUrl)
                        .error(R.drawable.no_image_placeholder)
                        .into(viewHolder.imageView);
            }
        }catch (NullPointerException e){
            Glide.with(context)
                    .load(Config.defaultMediaUrl)
                    .error(R.drawable.no_image_placeholder)
                    .into(viewHolder.imageView);
        }
        if (category.getName() != null) {
            viewHolder.title.setText(category.getName());
        }else {
            viewHolder.title.setText("Undefined");
        }
        viewHolder.count.setText(category.getCount()+"");
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView count,title;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.categoryBackground);
            this.cardView = view.findViewById(R.id.categoryItem);
            this.count = view.findViewById(R.id.categoryCount);
            this.title = view.findViewById(R.id.categoryTitle);
        }
    }
}
