package com.app.wplib.models.post;

import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Title{

	@SerializedName("rendered")
	private String rendered;

	public void setRendered(String rendered){
		this.rendered = Jsoup.parse(rendered).text();
	}

	public String getRendered(){
		return Jsoup.parse(rendered).text();
	}

	@Override
 	public String toString(){
		return 
			"Title{" + 
			"rendered = '" + rendered + '\'' + 
			"}";
		}
}