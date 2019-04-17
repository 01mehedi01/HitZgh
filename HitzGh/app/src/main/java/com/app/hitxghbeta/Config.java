package com.app.hitxghbeta;

/**
 * Created by anubhav on 29/12/17.
 */

public class Config {

    public static final String siteUrl = "http://hitxgh.com"; //no tariling slash
    public static final boolean enableRTL = false;
    public static int defaultHomeScreen = 2; //Choose from 1,2 or 3
    public static String TTSLanguage = "en";
    public static boolean showFBBannerAds = true;
    public static boolean enableSignupSignin = false;
    public static boolean enableIntroSlider = true;
    public static final int currentAppVersion = 4;
    public static final boolean isPluginInstalled = true;
    public static final boolean showBannerAds = true;
    public static final boolean showFullScreenAds = true;
    public static final int interstitialAdInterval = 60; //Shows interstitial ads every 20 seconds
    public static final boolean showLatestCategoryWhenNothingSelected = true;
    public static final boolean showLatestPostsInSliderWhenNoCategorySelected = true;
    public static boolean allowOnlySignedInUsersToComment = false;
    public static final String defaultMediaUrl = "https://images.pexels.com/photos/450597/pexels-photo-450597.jpeg?h=350&auto=compress&cs=tinysrgb";

    //Contants (Do not touch below this line)
    public static boolean showDialog = true;
    public static final String DEF_SHAREF_PREF = "wordroid_shared_pref";
    public static final String APP_NAME = "app_name";
    public static final String NO_OF_SECTIONS = "no_of_sections";
    public static final String APP_VERSION = "app_version";
    public static final String IS_SETTINGS_SAVED = "settings_saved";
    public static final String IS_FORCE_UPDATE = "force_update";
    public static final String TOOLBAR_COLOR = "app_name";
    public static final String SECTION_PREFIX = "section_";
    public static final String SECTION_TITLE = "_title";
    public static final String SECTION_SMALL_IMAGE_URL = "_smu";
    public static final String SECTION_CATEGORY= "_category";
    public static final String SECTION_TYPE = "_type";
    public static final String SECTION_COUNT = "_count";
    static final String SLIDER_CATEGORY = "slider_category";
    static final String IS_SLIDER_ENABLED = "is_slider_enabled";

    public static String getSiteUrl(){
        return siteUrl;
    }

}
