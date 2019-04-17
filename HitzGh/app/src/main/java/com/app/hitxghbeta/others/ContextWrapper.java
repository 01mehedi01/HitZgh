package com.app.hitxghbeta.others;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.Log;

import java.util.Locale;

/**
 * Created by anubhav on 10/03/18.
 */

public class ContextWrapper extends android.content.ContextWrapper {

    public ContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, String language) {
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();
        Locale newLocale = new Locale(language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context = context.createConfigurationContext(configuration);
            Log.e("LocaleConfig N",language);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);
            Log.e("LocaleConfig JB",language);
        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
            Log.e("LocaleConfig",language);
        }

        return new ContextWrapper(context);
    }

}
