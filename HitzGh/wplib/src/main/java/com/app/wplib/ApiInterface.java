package com.app.wplib;


import com.app.wplib.models.AuthApi;
import com.app.wplib.models.Settings;
import com.app.wplib.models.author.Author;
import com.app.wplib.models.category.Category;
import com.app.wplib.models.comment.Comment;
import com.app.wplib.models.media.Media;
import com.app.wplib.models.page.Page;
import com.app.wplib.models.post.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Anubhav on 19-07-2017.
 */

public interface ApiInterface {

    //Get Post List By Page, By Categories, By Search
    @GET("wp/v2/posts")
    Call<List<Post>> getPostList(
            @Query("page") Integer page,
            @Query("categories") String categories,
            @Query("search") String search,
            @Query("per_page") Integer perPage,
            @Query("context") String context,
            @Query("exclude") String postId
    );

    //Fetch Single Post by id
    @GET("wp/v2/posts/{id}")
    Call<Post> getPost(
            @Path("id") Integer postId,
            @Query("slug") String slug,
            @Query("password") String password
    );

    @GET("wp/v2/posts/")
    Call<List<Post>> getPostBySlug(
            @Query("slug") String slug,
            @Query("password") String password
    );

    //Fetch Media List
    @GET("wp/v2/media")
    Call<List<Media>> getMediaList(
            @Query("page") Integer page,
            @Query("orderby") String orderby,
            @Query("order") String order
    );

    //Fetch Single Media
    @GET("wp/v2/media/{id}")
    Call<Media> getMedia(
            @Path("id") Integer mediaId
    );

    //Fetch Single Author
    @GET("wp/v2/users/{id}")
    Call<Author> getAuthor(
            @Path("id") Integer authorId
    );

    //Fetch List of categories
    @GET("wp/v2/categories")
    Call<List<Category>> getCategoriesList(
            @Query("page") Integer page,
            @Query("search") String search,
            @Query("orderby") String orderby,
            @Query("parent") Integer parent,
            @Query("order") String order,
            @Query("hide_empty") Integer hide_empty,
            @Query("post") Integer post,
            @Query("slug") String slug
    );

    //Fetch Single Category
    @GET("wp/v2/categories/{id}")
    Call<Category> getCategory(
            @Path("id") Integer categoryId
    );

    @POST("wp/v2/comments")
    Call<Comment> postComment(
            @Query("post") int post,
            @Query("parent") int parent,
            @Query("author_name") String name,
            @Query("author_email") String email,
            @Query("content") String comment
    );

    @GET("wp/v2/comments")
    Call<List<Comment>> getCommentByPostId(
            @Query("post") Integer post,
            @Query("parent") Integer parent,
            @Query("page") Integer page,
            @Query("search") String search,
            @Query("orderby") String orderby
    );

    @GET("wp/v2/pages")
    Call<List<Page>> getpageList(
            @Query("page") Integer page,
            @Query("search") String search,
            @Query("context") String context
    );

    @GET("wp/v2/pages/{id}")
    Call<Page> getPageById(@Path("id") Integer id);

    @GET("wordroid/v2/settings")
    Call<Settings> getSettings();

    @GET("dashboard/api.php")
    Call<AuthApi> getAuth(
            @Query("key") String key
    );
}
