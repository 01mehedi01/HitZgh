package com.app.wplib.models.media;

import com.google.gson.annotations.SerializedName;


import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Media{

	@SerializedName("date")
	private String date;

	@SerializedName("post")
	private int post;

	@SerializedName("link")
	private String link;

	@SerializedName("id")
	private int id;

	@SerializedName("source_url")
	private String sourceUrl;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setPost(int post){
		this.post = post;
	}

	public int getPost(){
		return post;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setSourceUrl(String sourceUrl){
		this.sourceUrl = sourceUrl;
	}

	public String getSourceUrl(){
		return sourceUrl;
	}

	public Media getDefaultMedia(){
		Media m = new Media();
		m.setId(0);
		m.setPost(0);
		m.setSourceUrl("");
		m.setLink("https://images.pexels.com/photos/681638/pexels-photo-681638.jpeg?h=350&auto=compress&cs=tinysrgb");
		return m;
	}
}