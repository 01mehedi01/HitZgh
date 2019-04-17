package com.app.wplib.models;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SectionsItem{

	@SerializedName("image")
	private String image;

	@SerializedName("category_id")
	private String categoryId;

	@SerializedName("title")
	private String title;

	@SerializedName("image_id")
	private int imageId;

	@SerializedName("type")
	private String type;

	@SerializedName("post_count")
	private String count;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCategoryId(String categoryId){
		this.categoryId = categoryId;
	}

	public String getCategoryId(){
		return categoryId;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setImageId(int imageId){
		this.imageId = imageId;
	}

	public int getImageId(){
		return imageId;
	}

	@Override
 	public String toString(){
		return 
			"SectionsItem{" + 
			"image = '" + image + '\'' + 
			",category_id = '" + categoryId + '\'' + 
			",title = '" + title + '\'' + 
			",image_id = '" + imageId + '\'' + 
			"}";
		}
}