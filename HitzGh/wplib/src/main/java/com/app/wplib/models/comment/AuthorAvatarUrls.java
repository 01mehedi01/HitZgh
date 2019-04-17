package com.app.wplib.models.comment;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class AuthorAvatarUrls{

	@SerializedName("24")
	private String jsonMember24;

	@SerializedName("48")
	private String jsonMember48;

	@SerializedName("96")
	private String jsonMember96;

	public void setJsonMember24(String jsonMember24){
		this.jsonMember24 = jsonMember24;
	}

	public String getJsonMember24(){
		return jsonMember24;
	}

	public void setJsonMember48(String jsonMember48){
		this.jsonMember48 = jsonMember48;
	}

	public String getJsonMember48(){
		return jsonMember48;
	}

	public void setJsonMember96(String jsonMember96){
		this.jsonMember96 = jsonMember96;
	}

	public String getJsonMember96(){
		return jsonMember96;
	}
}