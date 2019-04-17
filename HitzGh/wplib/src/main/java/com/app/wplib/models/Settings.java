package com.app.wplib.models;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Settings{

	@SerializedName("app_version")
	private int appVersion;

	@SerializedName("user_key")
	private String user_key;

	@SerializedName("update_title")
	private String updateTitle;

	@SerializedName("update_body")
	private String updateBody;

	@SerializedName("settings_saved")
	private boolean setting_saved;

	@SerializedName("app_title")
	private String appTitle;

	@SerializedName("toolbar_color")
	private String toolbarColor;

	@SerializedName("force_update")
	private boolean forceUpdate;

	@SerializedName("slider_enabled")
	private boolean sliderEnabled;

	@SerializedName("slider_cat")
	private String sliderCategory;

	@SerializedName("sections")
	private List<SectionsItem> sections;

	@SerializedName("categories")
	private String[] categories;

	public String getUpdateTitle() {
		return updateTitle;
	}

	public void setUpdateTitle(String updateTitle) {
		this.updateTitle = updateTitle;
	}

	public String getUpdateBody() {
		return updateBody;
	}

	public void setUpdateBody(String updateBody) {
		this.updateBody = updateBody;
	}

	public boolean isSetting_saved() {
		return setting_saved;
	}

	public void setSetting_saved(boolean setting_saved) {
		this.setting_saved = setting_saved;
	}

	public String getUser_key() {
		return user_key;
	}

	public void setUser_key(String user_key) {
		this.user_key = user_key;
	}

	public boolean isSliderEnabled() {
		return sliderEnabled;
	}

	public void setSliderEnabled(boolean sliderEnabled) {
		this.sliderEnabled = sliderEnabled;
	}

	public String getSliderCategory() {
		return sliderCategory;
	}

	public void setSliderCategory(String sliderCategory) {
		this.sliderCategory = sliderCategory;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public int getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(int appVersion) {
		this.appVersion = appVersion;
	}

	public void setAppTitle(String appTitle){
		this.appTitle = appTitle;
	}

	public String getAppTitle(){
		return appTitle;
	}

	public void setToolbarColor(String toolbarColor){
		this.toolbarColor = toolbarColor;
	}

	public String getToolbarColor(){
		return toolbarColor;
	}

	public boolean isForceUpdate() {
		return forceUpdate;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}

	public void setSections(List<SectionsItem> sections){
		this.sections = sections;
	}

	public List<SectionsItem> getSections(){
		return sections;
	}

	@Override
 	public String toString(){
		return 
			"Settings{" + 
			"app_version = '" + appVersion + '\'' + 
			",app_title = '" + appTitle + '\'' + 
			",toolbar_color = '" + toolbarColor + '\'' + 
			",force_update = '" + forceUpdate + '\'' + 
			",sections = '" + sections + '\'' + 
			"}";
		}
}