package com.app.wplib.models.comment;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Content{

	@SerializedName("rendered")
	private String rendered;

	public void setRendered(String rendered){
		this.rendered = rendered;
	}

	public String getRendered(){
		return rendered;
	}
}