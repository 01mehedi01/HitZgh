package com.app.wplib.models.page;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Page{

	@SerializedName("date")
	private String date;

	@SerializedName("template")
	private String template;

	@SerializedName("parent")
	private int parent;

	@SerializedName("menu_order")
	private int menuOrder;

	@SerializedName("modified_gmt")
	private String modifiedGmt;

	@SerializedName("author")
	private int author;

	@SerializedName("author_name")
	private String authorName;

	@SerializedName("link")
	private String link;

	@SerializedName("type")
	private String type;

	@SerializedName("title")
	private Title title;

	@SerializedName("comment_status")
	private String commentStatus;

	@SerializedName("content")
	private Content content;

	@SerializedName("featured_media")
	private int featuredMedia;

	private String featuredMediaUrl;

	@SerializedName("ping_status")
	private String pingStatus;

	@SerializedName("modified")
	private String modified;

	@SerializedName("id")
	private int id;

	@SerializedName("excerpt")
	private Excerpt excerpt;

	@SerializedName("date_gmt")
	private String dateGmt;

	@SerializedName("slug")
	private String slug;

	@SerializedName("status")
	private String status;

	@SerializedName("better_featured_image")
	private BetterFeaturedImage betterFeaturedImage;

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public BetterFeaturedImage getBetterFeaturedImage() {
		return betterFeaturedImage;
	}

	public void setBetterFeaturedImage(BetterFeaturedImage betterFeaturedImage) {
		this.betterFeaturedImage = betterFeaturedImage;
	}

	public String getFeaturedMediaUrl() {
		return featuredMediaUrl;
	}

	public void setFeaturedMediaUrl(String featuredMediaUrl) {
		this.featuredMediaUrl = featuredMediaUrl;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setTemplate(String template){
		this.template = template;
	}

	public String getTemplate(){
		return template;
	}

	public void setParent(int parent){
		this.parent = parent;
	}

	public int getParent(){
		return parent;
	}

	public void setMenuOrder(int menuOrder){
		this.menuOrder = menuOrder;
	}

	public int getMenuOrder(){
		return menuOrder;
	}

	public void setModifiedGmt(String modifiedGmt){
		this.modifiedGmt = modifiedGmt;
	}

	public String getModifiedGmt(){
		return modifiedGmt;
	}

	public void setAuthor(int author){
		this.author = author;
	}

	public int getAuthor(){
		return author;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setTitle(Title title){
		this.title = title;
	}

	public Title getTitle(){
		return title;
	}

	public void setCommentStatus(String commentStatus){
		this.commentStatus = commentStatus;
	}

	public String getCommentStatus(){
		return commentStatus;
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

	public void setPingStatus(String pingStatus){
		this.pingStatus = pingStatus;
	}

	public String getPingStatus(){
		return pingStatus;
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

	public void setExcerpt(Excerpt excerpt){
		this.excerpt = excerpt;
	}

	public Excerpt getExcerpt(){
		return excerpt;
	}

	public void setDateGmt(String dateGmt){
		this.dateGmt = dateGmt;
	}

	public String getDateGmt(){
		return dateGmt;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}