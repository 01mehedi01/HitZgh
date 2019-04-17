package com.app.hitxghbeta.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.app.hitxghbeta.AddCommentActivity;
import com.app.hitxghbeta.CommentActivity;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.R;
import com.app.wplib.models.comment.Comment;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import org.jsoup.Jsoup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anubhav on 12/01/18.
 */

public class CommentItemAdapter extends AbstractItem<CommentItemAdapter, CommentItemAdapter.ViewHolder> {

    private Comment comment;
    private Context context;
    private int childCount;
    private String img;
    private String authorName;
    private String commentBody;

    public CommentItemAdapter(Context context,Comment comment){
        this.context = context;
        this.comment = comment;
        this.img = comment.getAuthorAvatarUrls().getJsonMember96();
        this.authorName = comment.getAuthorName();
        this.commentBody = comment.getContent().getRendered();
        if(Config.isPluginInstalled){
            this.childCount = comment.getChildCount();
        }else {
            this.childCount = comment.getChild();
        }
    }

    @Override
    public int getType() {
        return R.id.commentItem;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.comment_item;
    }

    //The logic to bind your data to the view
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        //call super so the selection is already handled for you
        super.bindView(viewHolder, payloads);
        viewHolder.authorName.setText(authorName);
        viewHolder.commentBody.setText(Jsoup.parse(commentBody).text());
        if(childCount>0) {
            viewHolder.replyBtn.setText(childCount + " Replies");
        }else{
            viewHolder.replyBtn.setText("Reply to this comment");
        }
        Glide.with(context)
                .load(img)
                .dontAnimate()
                .placeholder(R.color.md_green_300)
                .error(R.color.md_red_300)
                .into(viewHolder.avatar);
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

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView authorName,commentBody;
        CircleImageView avatar;
        Button replyBtn;
        public ViewHolder(View view) {
            super(view);
            this.cardView = view.findViewById(R.id.horizontalCarouselPostsItem);
            this.authorName = view.findViewById(R.id.author_title);
            this.commentBody = view.findViewById(R.id.commentsBodyTextView);
            this.avatar = view.findViewById(R.id.avatar_image);
            this.replyBtn = view.findViewById(R.id.replyBtn);
        }
    }

    public static class CommentItemClickEvent extends ClickEventHook<CommentItemAdapter>{

        @Override
        public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof CommentItemAdapter.ViewHolder) {
                return ((ViewHolder) viewHolder).replyBtn;
            }
            return null;
        }

        @Override
        public void onClick(View v, int position, FastAdapter<CommentItemAdapter> fastAdapter, CommentItemAdapter item) {
            if(item.childCount>0) {
                Intent intent = new Intent(item.context, CommentActivity.class);
                intent.putExtra(CommentActivity.ARG_POST, item.comment.getPost());
                intent.putExtra(CommentActivity.ARG_PAENT, item.comment.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                item.context.startActivity(intent);
            }else {
                Intent intent = new Intent(item.context,AddCommentActivity.class);
                intent.putExtra(AddCommentActivity.ARG_PARENT,item.comment.getId());
                intent.putExtra(AddCommentActivity.ARG_POST,item.comment.getPost());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                item.context.startActivity(intent);
            }
        }
    }
}
