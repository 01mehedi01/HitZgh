package com.app.wplib.models.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.wplib.models.author.Author;
import com.app.wplib.models.page.BetterFeaturedImage;
import com.app.wplib.models.page.CategoryDetails;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Post{

	@SerializedName("link")
	private String link;

	@SerializedName("date")
	private String date;

	@SerializedName("sticky")
	private boolean sticky;

	@SerializedName("type")
	private String type;

	@SerializedName("modified")
	private String modified;

	@SerializedName("id")
	private int id;

	@SerializedName("author_name")
	private String authorName;

	@SerializedName("categories")
	private List<Integer> categories = new ArrayList<>();

	private List<String> categoryString = new ArrayList<>();

	private String categories_id;

	private String categories_string;

	@SerializedName("title")
	private Title title;

	@SerializedName("excerpt")
	private Excerpt excerpt;

	@SerializedName("comment_status")
	private String commentStatus;

	@SerializedName("slug")
	private String slug;

	@SerializedName("content")
	private Content content;

	@SerializedName("featured_media")
	private int featuredMedia;

	@SerializedName("author")
	private int author;

	@SerializedName("better_featured_image")
	@Expose
	private BetterFeaturedImage betterFeaturedImage;

	@SerializedName("categories_detail")
	@Expose
	private List<CategoryDetails> categoryDetails = new ArrayList<>();

	private String password;

	private Author postAuthor;

	private String feat_media_url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BetterFeaturedImage getBetterFeaturedImage() {
		return betterFeaturedImage;
	}

	public void setBetterFeaturedImage(BetterFeaturedImage betterFeaturedImage) {
		this.betterFeaturedImage = betterFeaturedImage;
	}

	public List<CategoryDetails> getCategoryDetails() {
		return categoryDetails;
	}

	public void setCategoryDetails(List<CategoryDetails> categoryDetails) {
		this.categoryDetails = categoryDetails;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Author getPostAuthor() {
		return postAuthor;
	}

	public void setPostAuthor(Author postAuthor) {
		this.postAuthor = postAuthor;
	}

	public int getAuthor() {
		return author;
	}

	public void setAuthor(int author) {
		this.author = author;
	}

	public String getCategories_string() {
		return categories_string;
	}

	public void setCategories_string(String categories_string) {
		if(categories_string!=null)
			this.categories_string = Jsoup.parse(categories_string).text();
	}

	public String getFeat_media_url() {
		return feat_media_url;
	}

	public void setFeat_media_url(String feat_media_url) {
		this.feat_media_url = feat_media_url;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setSticky(boolean sticky){
		this.sticky = sticky;
	}

	public boolean isSticky(){
		return sticky;
	}

	public void setModified(String modified){
		this.modified = modified;
	}

	public String getModified(){
		return modified;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setCategories(List<Integer> categories){
		this.categories = categories;
	}

	public List<Integer> getCategories(){
		return categories;
	}

	public void setTitle(Title title){
		this.title = title;
	}

	public Title getTitle(){
		return title;
	}

	public void setExcerpt(Excerpt excerpt){
		this.excerpt = excerpt;
	}

	public Excerpt getExcerpt(){
		return excerpt;
	}

	public void setCommentStatus(String commentStatus){
		this.commentStatus = commentStatus;
	}

	public String getCommentStatus(){
		return commentStatus;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setContent(Content content){
		this.content = content;
	}

	public Content getContent(){
		return content;
	}

	public void setFeaturedMedia(int featuredMedia){
		this.featuredMedia = featuredMedia;
	}

	public int getFeaturedMedia(){
		return featuredMedia;
	}

    public List<String> getCategoryString() {
        return categoryString;
    }

    public void setCategoryString(List<String> categoryString) {
        this.categoryString = categoryString;
		String list2[] = new String[categoryString.size()];
		list2 = categoryString.toArray(list2);
		this.categories_string = Arrays.toString(list2);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCategories_id() {
		return categories_id;
	}

	public void setCategories_id() {
		Integer list2[] = new Integer[this.categories.size()];
		list2 = this.categories.toArray(list2);
		this.categories_id = Arrays.toString(list2);
	}
}