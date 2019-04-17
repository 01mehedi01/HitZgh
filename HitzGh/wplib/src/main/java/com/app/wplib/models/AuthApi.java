package com.app.wplib.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by anubhav on 02/02/18.
 */

public class AuthApi {

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("domain")
    private String domain;

    @SerializedName("package")
    private String app_package;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }

}
