package com.app.wplib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.wplib.models.category.Category;
import com.app.wplib.models.media.Media;
import com.app.wplib.models.page.BetterFeaturedImage;
import com.app.wplib.models.page.CategoryDetails;
import com.app.wplib.models.post.Content;
import com.app.wplib.models.post.Post;
import com.app.wplib.models.post.Title;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wp_details";

    // table name
    private static final String TABLE_POSTS = "posts";
    private static final String TABLE_MEDIA = "media";
    private static final String TABLE_CATEGORIES = " categories";
    private static final String TABLE_ARCHIVE_POSTS = "archives";
    private static final String TABLE_CATEGORY_POST_RELATION = "cp_rel";

    //Table Archives
    private static final String ARCHIVES_ID = "id";
    private static final String ARCHIVES_TITLE = "title";
    private static final String ARCHIVES_MEDIA_URL = "media_url";
    private static final String ARCHIVES_URL = "url";
    private static final String ARCHIVES_DATE = "date";
    private static final String ARCHIVES_CONTENT = "content";
    private static final String ARCHIVES_CAT = "category";
    private static final String ARCHIVE_CAT_ID = "cat_id";

    //Column names for media table
    private static final String MEDIA_ID = "media_id";
    private static final String MEDIA_POST = "post";
    private static final String MEDIA_DATE = "date";
    private static final String MEDIA_SOURCE = "source";
    private static final String MEDIA_LINK = "link";

    //Column names for categories table
    private static final String CATEGORIES_ID = "category_id";
    private static final String CATEGORIES_DATE = "date";
    private static final String CATEGORIES_COUNT = "count";
    private static final String CATEGORIES_NAME = "name";
    private static final String CATEGORIES_SLUG = "slug";
    private static final String CATEGORIES_PARENT = "parent";
    private static final String CATEGORIES_LINK = "link";

    // Posts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "post_link";
    private static final String KEY_DATE = "timedate";
    private static final String KEY_CATEGORY_IDS = "cat_ids";
    private static final String KEY_CATEGORIES = "firstCategory";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_FEAT_IMG = "featImg";

    //CP_REL Table Columns name
    private static final String REL_CAT_ID = "cat_id";
    private static final String REL_POST_ID = "post_id";
    private static final String REL_DATE = "date";

    //Operations Variables
    int offset = 0;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_POSTS + " ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_TITLE+" TEXT,"+KEY_DATE+" TEXT,"+KEY_CATEGORIES+" NUMBER,"+KEY_CATEGORY_IDS+" TEXT,"+KEY_AUTHOR+" TEXT,"+KEY_CONTENT+" TEXT,"+KEY_FEAT_IMG+" TEXT,\n" +
                ""+ KEY_LINK +" Text)";
        String CREATE_MEDIA_TABLE = "CREATE TABLE "+TABLE_MEDIA+" ("+MEDIA_ID+" INTEGER PRIMARY KEY, "+MEDIA_POST+" INTEGER,"+MEDIA_DATE+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"+MEDIA_LINK+" TEXT, "+MEDIA_SOURCE+" TEXT)";
        String CREATE_CAREGORIES_TABLE = "CREATE TABLE "+TABLE_CATEGORIES+" ("+CATEGORIES_ID+" INTEGER PRIMARY KEY, "+CATEGORIES_COUNT+" INTEGER, "+CATEGORIES_NAME+" TEXT, "+CATEGORIES_SLUG+" TEXT,"+CATEGORIES_PARENT+" INTEGER,"+CATEGORIES_LINK+" TEXT)";
        String CREATE_ARCIVES_TABLE = "CREATE TABLE " + TABLE_POSTS + " ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_TITLE+" TEXT,"+KEY_DATE+" TEXT,"+KEY_CONTENT+" TEXT,"+KEY_FEAT_IMG+" TEXT,\n" +
                ""+ KEY_LINK +" Text)";
        String CREATE_CP_REL = "CREATE TABLE "+TABLE_CATEGORY_POST_RELATION+" ("+REL_CAT_ID+" INTEGER, "+REL_POST_ID+","+REL_DATE+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, PRIMARY KEY ("+REL_CAT_ID+","+REL_POST_ID+"))";
        String CREATE_ARCHIVES = "CREATE TABLE "+TABLE_ARCHIVE_POSTS+" ("+ARCHIVES_ID+" INTEGER PRIMARY KEY, "+ARCHIVES_DATE+" TEXT, "+ARCHIVES_TITLE+" TEXT, "+ARCHIVES_MEDIA_URL+" TEXT,"+ARCHIVES_CONTENT+" TEXT, "+ARCHIVES_URL+" TEXT, "+ARCHIVES_CAT+" TEXT, "+ARCHIVE_CAT_ID+" INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_MEDIA_TABLE);
        db.execSQL(CREATE_CAREGORIES_TABLE);
        db.execSQL(CREATE_CP_REL);
        db.execSQL(CREATE_ARCHIVES);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARCHIVE_POSTS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addCPRelation(int cat,int post,String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REL_CAT_ID,cat);
        values.put(REL_POST_ID,post);
        values.put(REL_DATE,date);
        //db.insert(TABLE_CATEGORY_POST_RELATION,null,values);
        db.insertWithOnConflict(TABLE_CATEGORY_POST_RELATION,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    //Adding Data
    public void addPost(Post post){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID,post.getId());
        values.put(KEY_TITLE,post.getTitle().getRendered());
        values.put(KEY_DATE,post.getDate());
        values.put(KEY_LINK,post.getLink());
        values.put(KEY_FEAT_IMG,post.getFeaturedMedia());
        if(post.getCategories().size()>0) {
            values.put(KEY_CATEGORIES, post.getCategories().get(0));
            values.put(KEY_CATEGORY_IDS,post.getCategories_id());
        }
        //values.put(KEY_CONTENT,post.getContent().getRendered());
        values.put(KEY_AUTHOR,post.getAuthor());
        //db.insert(TABLE_POSTS, null, values);
        db.insertWithOnConflict(TABLE_POSTS,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        db.close(); // Closing database connection
    }

    public void addOfflinePost(Post post){
        Log.e("Media","Url: "+post.getFeat_media_url());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ARCHIVES_ID,post.getId());
        values.put(ARCHIVES_TITLE,post.getTitle().getRendered());
        values.put(ARCHIVES_DATE,post.getDate());
        values.put(ARCHIVES_URL,post.getLink());
        if(post.getFeat_media_url()!=null) {
            values.put(ARCHIVES_MEDIA_URL, post.getFeat_media_url());
        }else if(post.getBetterFeaturedImage()!=null&&post.getBetterFeaturedImage().getPostThumbnail()!=null){
            values.put(ARCHIVES_MEDIA_URL,post.getBetterFeaturedImage().getPostThumbnail());
        }
        values.put(ARCHIVES_CONTENT,post.getContent().getRendered());
        values.put(ARCHIVES_CAT,post.getCategories_string());
        values.put(ARCHIVE_CAT_ID,post.getCategories().get(0));
        Log.e("Category","S: "+post.getCategories_string());
        //db.insert(TABLE_POSTS, null, values);
        db.insertWithOnConflict(TABLE_ARCHIVE_POSTS,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        db.close(); // Closing database connection
    }

    public void addMedia(Media media){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MEDIA_ID, media.getId());
            values.put(MEDIA_POST, media.getPost());
            values.put(MEDIA_LINK, media.getLink());
            values.put(MEDIA_SOURCE, media.getSourceUrl());
            values.put(MEDIA_DATE,media.getDate());
            //db.insert(TABLE_MEDIA, null, values);
            db.insertWithOnConflict(TABLE_MEDIA,null,values,SQLiteDatabase.CONFLICT_REPLACE);
            db.close(); // Closing database connection
    }

    public void addCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_ID,category.getId());
        values.put(CATEGORIES_COUNT,category.getCount());
        values.put(CATEGORIES_NAME,category.getName());
        values.put(CATEGORIES_LINK,category.getLink());
        values.put(CATEGORIES_SLUG,category.getSlug());
        values.put(CATEGORIES_PARENT,category.getParent());
        //db.insert(TABLE_CATEGORIES, null, values);
        db.insertWithOnConflict(TABLE_CATEGORIES,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection
    }

    /*public List<Post> getPostsByCategories(int[] ids,int count){

    }*/

    public List<Post> getPostsByCategory(int id,int start,int offset){
        this.offset +=offset;
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_POSTS+ " WHERE "+CATEGORIES_ID+" = "+id+" OFFSET "+this.offset+" ROWS";
        Cursor cursor = db.rawQuery(query,null);
        cursor = db.query(true, TABLE_POSTS, new String[] { KEY_ID,
                        KEY_CATEGORY_IDS }, KEY_CATEGORY_IDS + " LIKE ?",
                new String[] { "%"+id+"%" }, null, null, null,
                null);
        if(cursor!=null&&cursor.getCount()>0){
            if (cursor.moveToFirst()) {
                do {
                    Post post = new Post();
                    Title title = new Title();
                    Content content = new Content();
                    Media media = new Media();
                    //ID Set
                    post.setId(cursor.getInt(0));
                    //Title SET
                    title.setRendered(cursor.getString(1));
                    post.setTitle(title);
                    //Date Set
                    post.setModified(cursor.getString(2));
                    //Content Set
                    content.setRendered(cursor.getString(3));
                    post.setContent(content);
                    //Feat Img Set
                    post.setFeaturedMedia(cursor.getInt(4));
                    media = getMedia(cursor.getInt(4));
                    post.setFeat_media_url(media.getSourceUrl());
                    // Adding contact to list
                    if(cursor.getString(0)!= null) {
                        postList.add(post);
                    }
                } while (cursor.moveToNext());
            }
        }
        return postList;
    }

    public void updateCategoryString(Post post){
        Log.e("Databsase","Updating Categories");
        addPost(post);
    }

    //Fetching Data By ID
    public Media getMedia(int id){
        Media media = new Media();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_MEDIA+ " WHERE "+MEDIA_ID+" = "+id;
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null) {
            cursor.moveToFirst();
            if(cursor.getCount()>0) {
                media.setId(cursor.getInt(0));
                media.setPost(cursor.getInt(1));
                media.setDate(cursor.getString(2));
                media.setLink(cursor.getString(3));
                media.setSourceUrl(cursor.getString(4));
            }else{
                db.close();
                cursor.close();
                return null;
            }

        }else{
            db.close();
            return null;
        }
        db.close();
        cursor.close();
        return media;
    }

    public Category getCategory(int id){
        Category category = new Category();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORIES+ " WHERE "+CATEGORIES_ID+" = "+id;
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount()>0) {
                category.setId(cursor.getInt(0));
                category.setCount(cursor.getInt(1));
                category.setName(cursor.getString(2));
                category.setSlug(cursor.getString(3));
                category.setParent(cursor.getInt(4));
                category.setLink(cursor.getString(5));
                category.setChildCategories(getChilds(id));
            }else{
                db.close();
                cursor.close();
                return null;
            }
        }else{
            db.close();
            return null;
        }
        db.close();
        cursor.close();
        return category;
    }


    public List<Category> getAllCategories(Integer count){
        List<Category> allcats = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if(count==null) {
            query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + CATEGORIES_PARENT + "=0 ORDER BY name";
        }else{
            query = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + CATEGORIES_PARENT + "=0 ORDER BY name LIMIT 0,"+count;
        }
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null){
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                do{
                    Category category = new Category();
                    category.setId(cursor.getInt(0));
                    category.setCount(cursor.getInt(1));
                    category.setName(cursor.getString(2));
                    category.setSlug(cursor.getString(3));
                    category.setParent(cursor.getInt(4));
                    category.setLink(cursor.getString(5));
                    category.setChildCategories(getChilds(cursor.getInt(0)));
                    allcats.add(category);
                }while (cursor.moveToNext());
            }
        }
        return allcats;
    }


    public List<Category> getChilds(int parentId){
        List<Category> childs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_CATEGORIES+ " WHERE "+CATEGORIES_PARENT+" = "+parentId;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null){
            cursor.moveToFirst();
            if(cursor.getCount()>0){
                do{
                    Category category = new Category();
                    category.setId(cursor.getInt(0));
                    category.setCount(cursor.getInt(1));
                    category.setName(cursor.getString(2));
                    category.setSlug(cursor.getString(3));
                    category.setParent(cursor.getInt(4));
                    category.setLink(cursor.getString(5));
                    childs.add(category);
                }while (cursor.moveToNext());
            }
        }
        return childs;
    }

    // Getting single contact
    public Post getPost(int id) {
        Post post = new Post();
        Title title = new Title();
        Content content = new Content();
        List<Integer> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_ARCHIVE_POSTS+" WHERE "+ARCHIVES_ID+" = "+id;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null){
            cursor.moveToFirst();
            if(cursor.getCount()>0) {
                post.setId(cursor.getInt(0));
                post.setDate(cursor.getString(1));
                title.setRendered(cursor.getString(2));
                post.setTitle(title);
                BetterFeaturedImage betterFeaturedImage = new BetterFeaturedImage();
                betterFeaturedImage.setPostThumbnail(cursor.getString(3));
                post.setBetterFeaturedImage(betterFeaturedImage);
                post.setFeat_media_url(cursor.getString(3));
                content.setRendered(cursor.getString(4));
                post.setContent(content);
                post.setLink(cursor.getString(5));
                post.setCategories_string(cursor.getString(6));
                list.add(cursor.getInt(7));
                post.setCategories(list);
            }else{
                db.close();
                cursor.close();
                return null;
            }
        }else{
            db.close();
            return null;
        }
        db.close();
        cursor.close();
        return post;
    }

    public List<Post> getOfflinePosts() {
        List<Post> postList = new ArrayList<Post>();
        // Select All Query
        String selectQuery = "SELECT * FROM "+TABLE_ARCHIVE_POSTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                Title t = new Title();
                Content content = new Content();
                BetterFeaturedImage betterFeaturedImage = new BetterFeaturedImage();
                List<Integer> list = new ArrayList<>();
                List<CategoryDetails> categoryDetails = new ArrayList<>();
                post.setId(cursor.getInt(0));
                post.setDate(cursor.getString(1));
                t.setRendered(cursor.getString(2));
                post.setTitle(t);
                post.setFeat_media_url(cursor.getString(3));
                betterFeaturedImage.setPostThumbnail(cursor.getString(3));
                post.setBetterFeaturedImage(betterFeaturedImage);
                content.setRendered(cursor.getString(4));
                post.setContent(content);
                post.setLink(cursor.getString(5));
                post.setCategories_string(cursor.getString(6));
                post.setCategoryDetails(categoryDetails);
                list.add(cursor.getInt(7));
                post.setCategories(list);
                if(cursor.getString(0)!= null) {
                    postList.add(post);
                }
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return postList;
    }

    // Getting All Contacts
    public List<Post> getAllPosts(int start,int offset) {
        List<Post> postList = new ArrayList<Post>();
        // Select All Query
        String selectQuery = "SELECT p.id,p.title,p.timedate,p.post_link,c.name,m.source FROM posts p\n" +
                "LEFT JOIN categories c\n" +
                "ON p.firstCategory = c.category_id\n" +
                "LEFT JOIN media m\n" +
                "\tON p.featImg = m.media_id\n" +
                "order by p.timedate desc LIMIT "+start+","+offset+";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Post post = new Post();
                Title t = new Title();
                List<Integer> list = new ArrayList<>();
                //Set Id
                post.setId(cursor.getInt(0));
                //Set Title
                t.setRendered(cursor.getString(1));
                post.setTitle(t);
                //Set Date
                post.setDate(cursor.getString(2));
                //Set Link
                post.setLink(cursor.getString(3));
                //Set Category Name
                post.setCategories_string(cursor.getString(4));
                //Set Featured Image
                post.setFeat_media_url(cursor.getString(5));
                // Adding contact to list
                if(cursor.getString(0)!= null) {
                    postList.add(post);
                }
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return postList;
    }

    /*// Updating single contact
    public int updateContact(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, post.getId()); // Contact Name
        values.put(KEY_TITLE, post.getTitle()); // Contact Phone
        values.put(KEY_DATE,post.getDate());
        values.put(KEY_CONTENT,post.getContent());
        values.put(KEY_FEAT_IMG,post.getFeatImg());
        values.put(KEY_LINK,post.getCommentCount());

        // updating row
        return db.update(TABLE_POSTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(post.getId()) });
    }*/

    public void deletePost(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ARCHIVE_POSTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    // Getting Count
    public int getPostsCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_POSTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int getMediaCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_MEDIA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }
    public int getCategoryCount() {
        int count;
        String countQuery = "SELECT  * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    private String getDatabaseSummary(){
        String s = "Media Count: "+getMediaCount()+"\n"+
                "Category Count: "+getCategoryCount()+"\n"+
                "Posts Count: "+getPostsCount();
        return s;
    }

}
