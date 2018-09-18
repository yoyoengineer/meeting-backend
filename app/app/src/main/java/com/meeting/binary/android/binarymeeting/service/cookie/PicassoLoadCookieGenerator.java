package com.meeting.binary.android.binarymeeting.service.cookie;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by meldi on 3/30/2018.
 */

public class PicassoLoadCookieGenerator {

    private static final String PREF_COOKIES = "PREF_COOKIES";

    public static final String TAG = "test_tag_cookie";

    public static Picasso getImageLoader(Context context) {

        String recupCookie = CookiePreferences.getStoredCookie(context);
//                PreferenceManager.getDefaultSharedPreferences(context)
//                .getString(PREF_COOKIES, null);


        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new UrlConnectionDownloader(context) {
            @Override
            protected HttpURLConnection openConnection(Uri uri) throws IOException {
                HttpURLConnection connection = super.openConnection(uri);
                if (recupCookie != null){
                    Log.i(TAG, "intercept: " + recupCookie);
                    connection.setRequestProperty("Cookie", recupCookie);
                }
                Log.i(TAG, "openConnection: " + uri.toString());
                return connection;
            }
        });
        return builder.build();
    }
}
