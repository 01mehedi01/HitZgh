package com.app.wplib.models.page;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by anubhav on 21/01/18.
 */

public class BetterFeaturedImage {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("alt_text")
    @Expose
    private String altText;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("source_url")
    @Expose
    private String sourceUrl;
    @SerializedName("medium_large")
    @Expose
    private String mediumLarge;
    @SerializedName("post_thumbnail")
    @Expose
    private String postThumbnail;

    public String getMediumLarge() {
        return mediumLarge;
    }

    public void setMediumLarge(String mediumLarge) {
        this.mediumLarge = mediumLarge;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getPostThumbnail() {
        return postThumbnail;
    }

    public void setPostThumbnail(String postThumbnail) {
        this.postThumbnail = postThumbnail;
    }

}
