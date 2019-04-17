package com.app.wplib.models.comment;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Comment{

	@SerializedName("author_name")
	private String authorName;

	@SerializedName("date")
	private String date;

	@SerializedName("parent")
	private int parent;

	@SerializedName("post_title")
	private String postTitle;

	@SerializedName("author")
	private int author;

	@SerializedName("link")
	private String link;

	@SerializedName("type")
	private String type;

	@SerializedName("content")
	private Content content;

	@SerializedName("author_url")
	private String authorUrl;

	@SerializedName("post")
	private int post;

	@SerializedName("author_avatar_urls")
	private AuthorAvatarUrls authorAvatarUrls;

	@SerializedName("id")
	private int id;

	@SerializedName("date_gmt")
	private String dateGmt;

	@SerializedName("status")
	private String status;

	@SerializedName("child_comment_count")
	private int childCount;

	private int child;

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public int getChild() {
		return child;
	}

	public void setChild(int child) {
		this.child = child;
	}

	public void setAuthorName(String authorName){
		this.authorName = authorName;
	}

	public String getAuthorName(){
		return authorName;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setParent(int parent){
		this.parent = parent;
	}

	public int getParent(){
		return parent;
	}

	public void setPostTitle(String postTitle){
		this.postTitle = postTitle;
	}

	public String getPostTitle(){
		return postTitle;
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

	public void setContent(Content content){
		this.content = content;
	}

	public Content getContent(){
		return content;
	}

	public void setAuthorUrl(String authorUrl){
		this.authorUrl = authorUrl;
	}

	public String getAuthorUrl(){
		return authorUrl;
	}

	public void setPost(int post){
		this.post = post;
	}

	public int getPost(){
		return post;
	}

	public void setAuthorAvatarUrls(AuthorAvatarUrls authorAvatarUrls){
		this.authorAvatarUrls = authorAvatarUrls;
	}

	public AuthorAvatarUrls getAuthorAvatarUrls(){
		return authorAvatarUrls;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setDateGmt(String dateGmt){
		this.dateGmt = dateGmt;
	}

	public String getDateGmt(){
		return dateGmt;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}