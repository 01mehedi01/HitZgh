package com.app.hitxghbeta.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.app.hitxghbeta.ApiClient;
import com.app.hitxghbeta.CarouselPostListActivity;
import com.app.hitxghbeta.Colors;
import com.app.hitxghbeta.Config;
import com.app.hitxghbeta.PostActivity;
import com.app.hitxghbeta.PostListActivity;
import com.app.hitxghbeta.R;
import com.app.wplib.ApiInterface;
import com.app.wplib.GetRecentPost;
import com.app.wplib.models.post.Post;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.List;

import static com.app.hitxghbeta.MainApplication.POST_IDS;

public class ContentBox1 extends Fragment {

    private static final String ARG_TITLE = "title_arg";
    private static final String ARG_ICON = "icon_arg";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_POSTS_COUNT = "noOfPosts";
    private static final String ARG_POST_TYPE = "postType";

    private boolean isActive;
    private String title;
    private String imgUrl;
    private String category;
    private int noOfPosts;
    private int type;

    private TextView titleView;
    private ImageView iconView;
    private LinearLayout contanerLayout;
    private CardView outerContainet;
    private CardView mainLayout;
    private Button showMoreBtn;
    private View errorview;
    private TextView errorMsgView;
    private Button reloadBtn;
    private List<Post> postsList = new ArrayList<>();

    public ContentBox1() {
        // Required empty public constructor
    }

    public static ContentBox1 newInstance(String title,String iconUrl, String category, int noOfPosts,int type) {
        ContentBox1 fragment = new ContentBox1();
        Bundle args = new Bundle();

        args.putString(ARG_TITLE, title);
        args.putString(ARG_ICON, iconUrl);
        args.putString(ARG_CATEGORY, category);
        args.putInt(ARG_POSTS_COUNT, noOfPosts);
        args.putInt(ARG_POST_TYPE,type);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            imgUrl = getArguments().getString(ARG_ICON);
            category = getArguments().getString(ARG_CATEGORY);
            noOfPosts = getArguments().getInt(ARG_POSTS_COUNT);
            type = getArguments().getInt(ARG_POST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_box1, container, false);
        titleView = view.findViewById(R.id.contentBox1Title);
        iconView = view.findViewById(R.id.contentBoxIcon);
        contanerLayout = view.findViewById(R.id.rowsContainer);
        outerContainet = view.findViewById(R.id.outerContainer);
        mainLayout = view.findViewById(R.id.contentBoxMainCard);
        showMoreBtn = view.findViewById(R.id.showMoreBtn);
        showMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostListActivity.class);
                intent.putExtra(PostListActivity.ARG_TITLE,title);
                intent.putExtra(PostListActivity.ARG_CATEGORY,category);
                intent.putExtra(PostListActivity.ARG_TYPE,type);
                startActivity(intent);
            }
        });
        errorview = getLayoutInflater().inflate(R.layout.error_loading_layout,null);
        errorMsgView = errorview.findViewById(R.id.errorMessageView);
        reloadBtn = errorview.findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadBtnClicked();
            }
        });
        setUpViews();
        sendRequest();
        return view;
    }

    private void reloadBtnClicked(){
        contanerLayout.removeAllViews();
        sendRequest();
    }

    private void addLoadingViews(){
        if(type==1) {
            for (int i = 0; i < 3; i++) {
                View view = getLayoutInflater().inflate(R.layout.vl_type_item_loading, null);
                contanerLayout.addView(view);
            }
        }else if(type==2){
            for (int i=0;i<noOfPosts;i++){
                View view = getLayoutInflater().inflate(R.layout.vl_type_item_2_loading, null);
                contanerLayout.addView(view);
            }
        }else if(type==3){
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setHorizontalScrollBarEnabled(false);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.RIGHT);
            for (int i = 0; i < 3; i++) {
                //contanerLayout.setOrientation(LinearLayout.HORIZONTAL);
                View view = getLayoutInflater().inflate(R.layout.horizontal_posts_item_loading, null);
                linearLayout.addView(view);
            }
            horizontalScrollView.addView(linearLayout);
            contanerLayout.addView(horizontalScrollView);
        }else if (type==4){
            mainLayout.setVisibility(View.GONE);
        }
    }

    private void getPostIdList(List<Post> postsList){
        POST_IDS.clear();
        for (Post p:postsList){
            POST_IDS.add(p.getId());
        }
    }


    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(int position) {
            final int i=position;
            View view = getLayoutInflater().inflate(R.layout.slider_item,null);

            ImageView sliderImage = view.findViewById(R.id.sliderImage);
            TextView sliderCategory = view.findViewById(R.id.sliderCategory);
            TextView sliderTitle = view.findViewById(R.id.sliderTitle);
            LinearLayout textContainer = view.findViewById(R.id.sliderContainerLinearLayout);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0,0,0,0);
            textContainer.setLayoutParams(params);

            String title,categoryName,img;
            title = postsList.get(i).getTitle().getRendered();
            if(Config.isPluginInstalled){
                if(postsList.get(i).getCategoryDetails().size()>0) {
                    categoryName = postsList.get(i).getCategoryDetails().get(0).getName();
                }else {
                    categoryName = "Undefined";
                }
                if(postsList.get(i).getBetterFeaturedImage()!=null) {
                    img = postsList.get(i).getBetterFeaturedImage().getPostThumbnail();
                }else {
                    img = null;
                }
            }else {
                categoryName = postsList.get(i).getCategories_string();
                img = postsList.get(i).getFeat_media_url();
            }

            sliderTitle.setText(title);
            sliderCategory.setText(categoryName);
            Glide.with(getActivity())
                    .load(img)
                    .placeholder(R.color.md_green_100)
                    .error(R.drawable.no_image_placeholder)
                    .into(sliderImage);

            sliderCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CarouselPostListActivity.class);
                    intent.putExtra(PostListActivity.ARG_TITLE,postsList.get(i).getCategories_string());
                    intent.putExtra(PostListActivity.ARG_CATEGORY,postsList.get(i).getCategories().get(0)+"");
                    startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPostIdList(postsList);
                    Intent intent = new Intent(getActivity(),PostActivity.class);
                    intent.putExtra(PostActivity.POST_ID_STRING,postsList.get(i).getId());
                    intent.putExtra(PostActivity.POST_URL_STRING,postsList.get(i).getLink());
                    intent.putExtra(PostActivity.POST_PASSWORD_SRING,postsList.get(i).getPassword());
                    startActivity(intent);
                }
            });
            return view;
        }
    };

    private void setUpViews(){
        if(title!=null){
            titleView.setText(title);
        }else {
            titleView.setText("Untitled");
        }
        if (imgUrl==null){
            Glide.with(getActivity())
                    .load(Config.defaultMediaUrl)
                    .placeholder(R.color.md_green_100)
                    .error(R.drawable.no_image_placeholder)
                    .into(iconView);
        }else {
            Glide.with(getActivity())
                    .load(imgUrl)
                    .placeholder(R.color.md_green_100)
                    .error(R.drawable.no_image_placeholder)
                    .into(iconView);
        }
    }

    private void sendRequest(){
        addLoadingViews();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        GetRecentPost getRecentPost = new GetRecentPost(apiInterface,getActivity());
        getRecentPost.setPluginInstalled(Config.isPluginInstalled);
        getRecentPost.setOnCompleteListner(new GetRecentPost.Listner() {
            @Override
            public void onSuccessful(List<Post> postList, int totalPosts, int totalPages) {
                if(postList.size()>0&&isActive) {
                    addViews(postList);
                }
            }

            @Override
            public void onError(String msg) {
                contanerLayout.removeAllViews();
                contanerLayout.addView(errorview);
                //contanerLayout.setVisibility(View.GONE);
            }
        });
        getRecentPost.setCategory(category);
        getRecentPost.setPerPage(noOfPosts);
        getRecentPost.setCategoryEnabled(true);
        getRecentPost.setMediaEnabled(true);
        getRecentPost.setPage(1);
        getRecentPost.execute();
    }

    private void addViews(final List<Post> postList){
        contanerLayout.removeAllViews();
        if(type==1&&isActive) {
            for (int i = 0; i < postList.size(); i++) {
                final int currentItem = i;
                View view = getLayoutInflater().inflate(R.layout.vl_type_item, null);
                final TextView postTitle = view.findViewById(R.id.vl_title);
                final ImageView imageView = view.findViewById(R.id.vl_image);
                final TextView category = view.findViewById(R.id.vl_category);
                CardView cardView = view.findViewById(R.id.vl_category_cardview);
                String title,categoryName,img=null;
                title = postList.get(i).getTitle().getRendered();
                if(Config.isPluginInstalled){
                    if(postList.get(i).getCategoryDetails().size()>0) {
                        categoryName = postList.get(i).getCategoryDetails().get(0).getName();
                    }else {
                        categoryName = "Undefined";
                    }if(postList.get(i).getBetterFeaturedImage()!=null) {
                        img = postList.get(i).getBetterFeaturedImage().getPostThumbnail();
                    }else {
                        img = null;
                    }
                }else {
                    categoryName = postList.get(i).getCategories_string();
                    img = postList.get(i).getFeat_media_url();
                }
                postTitle.setText(title);
                category.setText(categoryName);
                Glide.with(getActivity())
                        .load(img)
                        .placeholder(R.color.md_green_100)
                        .error(R.drawable.no_image_placeholder)
                        .into(imageView);
                Colors r = new Colors();
                cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(),r.getRandomColor()));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(postList.get(currentItem).getExcerpt().isJsonMemberProtected()){
                            Toast.makeText(getActivity(),"Post Protected",Toast.LENGTH_SHORT).show();
                        }else {
                            getPostIdList(postList);
                            Intent intent = new Intent(getActivity(), PostActivity.class);
                            intent.putExtra(PostActivity.POST_ID_STRING,postList.get(currentItem).getId());
                            intent.putExtra(PostActivity.POST_URL_STRING,postList.get(currentItem).getLink());
                            startActivity(intent);
                        }
                    }
                });
                contanerLayout.addView(view);
            }
        }else if(2==type&&isActive){
            for (int i = 0; i < postList.size(); i++) {
                final int currentItem = i;
                View view = getLayoutInflater().inflate(R.layout.vl_type_item_2, null);
                TextView postTitle = view.findViewById(R.id.vl_textview_2);
                final ImageView imageView = view.findViewById(R.id.vl_imgview_2);
                TextView category = view.findViewById(R.id.vl_category_2);
                CardView cardView = view.findViewById(R.id.vl_category_cardview_2);

                String title,categoryName,img;
                title = postList.get(i).getTitle().getRendered();
                if(Config.isPluginInstalled){
                    if(postList.get(i).getCategoryDetails().size()>0) {
                        categoryName = postList.get(i).getCategoryDetails().get(0).getName();
                    }else {
                        categoryName = "Undefined";
                    }if(postList.get(i).getBetterFeaturedImage()!=null) {
                        img = postList.get(i).getBetterFeaturedImage().getPostThumbnail();
                    }else {
                        img = null;
                    }
                }else {
                    categoryName = postList.get(i).getCategories_string();
                    img = postList.get(i).getFeat_media_url();
                }
                postTitle.setText(title);
                category.setText(categoryName);
                Glide.with(getActivity())
                        .load(img)
                        .placeholder(R.color.md_green_100)
                        .error(R.drawable.no_image_placeholder)
                        .into(imageView);
                Colors r = new Colors();
                cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(),r.getRandomColor()));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(postList.get(currentItem).getExcerpt().isJsonMemberProtected()){
                            Toast.makeText(getActivity(),"Post Protected",Toast.LENGTH_SHORT).show();
                        }else {
                            getPostIdList(postList);
                            Intent intent = new Intent(getActivity(), PostActivity.class);
                            intent.putExtra(PostActivity.POST_ID_STRING,postList.get(currentItem).getId());
                            intent.putExtra(PostActivity.POST_URL_STRING,postList.get(currentItem).getLink());
                            startActivity(intent);
                        }
                    }
                });
                contanerLayout.addView(view);
            }
        }else if(3==type&&isActive){
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setHorizontalScrollBarEnabled(false);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.RIGHT);
            for (int i = 0; i < postList.size(); i++) {
                contanerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    final int currentItem = i;
                    View view = getLayoutInflater().inflate(R.layout.horizontal_posts_item, null);
                    TextView postTitle = view.findViewById(R.id.hpi_title);
                    final ImageView imageView = view.findViewById(R.id.hpi_image);

                String title,img;
                title = postList.get(i).getTitle().getRendered();
                if(Config.isPluginInstalled){
                    if(postList.get(i).getBetterFeaturedImage()!=null) {
                        img = postList.get(i).getBetterFeaturedImage().getPostThumbnail();
                    }else {
                        img = null;
                    }
                }else {
                    img = postList.get(i).getFeat_media_url();
                }
                postTitle.setText(title);
                Glide.with(getActivity())
                        .load(img)
                        .placeholder(R.color.md_green_100)
                        .error(R.drawable.no_image_placeholder)
                        .into(imageView);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(postList.get(currentItem).getExcerpt().isJsonMemberProtected()){
                                Toast.makeText(getActivity(),"Post Protected",Toast.LENGTH_SHORT).show();
                            }else {
                                getPostIdList(postList);
                                Intent intent = new Intent(getActivity(), PostActivity.class);
                                intent.putExtra(PostActivity.POST_ID_STRING,postList.get(currentItem).getId());
                                intent.putExtra(PostActivity.POST_URL_STRING,postList.get(currentItem).getLink());
                                startActivity(intent);
                            }
                        }
                    });
                linearLayout.addView(view);
            }
            horizontalScrollView.addView(linearLayout);
            contanerLayout.addView(horizontalScrollView);
        }else if(type==4&&isActive){
            if(postList.size()>0) {
                postsList.addAll(postList);
                View view = getLayoutInflater().inflate(R.layout.carousel_layout, null);
                CarouselView carouselView = view.findViewById(R.id.carouselViewInLayout);
                carouselView.setPageCount(noOfPosts);
                carouselView.setViewListener(viewListener);
                outerContainet.addView(view);
                outerContainet.setVisibility(View.VISIBLE);
            }
        }else if(type==5&&isActive){
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            LinearLayout linearLayout = new LinearLayout(getActivity());
            linearLayout.setHorizontalScrollBarEnabled(false);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.END);
            for (int i = 0; i < postList.size(); i++) {
                contanerLayout.setOrientation(LinearLayout.HORIZONTAL);
                final int currentItem = i;
                View view = getLayoutInflater().inflate(R.layout.carousel_item, null);
                TextView postTitle = view.findViewById(R.id.carousel_title);
                TextView category = view.findViewById(R.id.carousel_category);
                final ImageView imageView = view.findViewById(R.id.carousel_image);
                CardView cardView = view.findViewById(R.id.carouselItemCategoryCard);

                Colors r = new Colors();
                cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(),r.getRandomColor()));
                String title,categoryName,img;
                title = postList.get(i).getTitle().getRendered();
                if(Config.isPluginInstalled){
                    if(postList.get(i).getCategoryDetails().size()>0) {
                        categoryName = postList.get(i).getCategoryDetails().get(0).getName();
                    }else {
                        categoryName = "Undefined";
                    }if(postList.get(i).getBetterFeaturedImage()!=null) {
                        img = postList.get(i).getBetterFeaturedImage().getPostThumbnail();
                    }else {
                        img = null;
                    }
                }else {
                    categoryName = postList.get(i).getCategories_string();
                    img = postList.get(i).getFeat_media_url();
                }
                postTitle.setText(title);
                category.setText(categoryName);
                Glide.with(getActivity())
                        .load(img)
                        .placeholder(R.color.md_green_100)
                        .error(R.drawable.no_image_placeholder)
                        .into(imageView);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(postList.get(currentItem).getExcerpt().isJsonMemberProtected()){
                            Toast.makeText(getActivity(),"Post Protected",Toast.LENGTH_SHORT).show();
                        }else {
                            getPostIdList(postList);
                            Intent intent = new Intent(getActivity(), PostActivity.class);
                            intent.putExtra(PostActivity.POST_ID_STRING,postList.get(currentItem).getId());
                            intent.putExtra(PostActivity.POST_URL_STRING,postList.get(currentItem).getLink());
                            startActivity(intent);
                        }
                    }
                });
                linearLayout.addView(view);
            }
            horizontalScrollView.addView(linearLayout);
            contanerLayout.addView(horizontalScrollView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isActive=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive=true;
    }
}
