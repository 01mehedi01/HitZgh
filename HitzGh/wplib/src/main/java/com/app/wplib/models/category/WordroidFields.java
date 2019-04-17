package com.app.wplib.models.category;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class WordroidFields{

	@SerializedName("category_image")
	private String categoryImage;

	@SerializedName("hide_category")
	private String hideCategory;

	@SerializedName("category_color")
	private String categoryColor;

	public void setCategoryImage(String categoryImage){
		this.categoryImage = categoryImage;
	}

	public String getCategoryImage(){
		return categoryImage;
	}

	public void setHideCategory(String hideCategory){
		this.hideCategory = hideCategory;
	}

	public String getHideCategory(){
		return hideCategory;
	}

	public void setCategoryColor(String categoryColor){
		this.categoryColor = categoryColor;
	}

	public String getCategoryColor(){
		return categoryColor;
	}

}