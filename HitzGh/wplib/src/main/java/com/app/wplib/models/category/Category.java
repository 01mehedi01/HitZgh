package com.app.wplib.models.category;

import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Category{

	@SerializedName("date")
	private String date;

	@SerializedName("parent")
	private int parent;

	@SerializedName("count")
	private int count;

	@SerializedName("link")
	private String link;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	@SerializedName("cmb2")
	private Cmb2 cmb2;

	public void setCmb2(Cmb2 cmb2){
		this.cmb2 = cmb2;
	}

	public Cmb2 getCmb2(){
		return cmb2;
	}

	private List<Category> childCategories = new ArrayList<>();

	public List<Category> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(List<Category> childCategories) {
		this.childCategories = childCategories;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setParent(int parent){
		this.parent = parent;
	}

	public int getParent(){
		return parent;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return Jsoup.parse(name).text();
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}
}