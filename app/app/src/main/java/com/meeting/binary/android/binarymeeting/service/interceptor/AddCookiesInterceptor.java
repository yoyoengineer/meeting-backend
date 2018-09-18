package com.meeting.binary.android.binarymeeting.service.interceptor;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by loyck-daryl on 2018/3/8.
 */

public class AddCookiesInterceptor implements Interceptor {

    private static final String TAG = "tag_cookie";

    private Context context;

    public AddCookiesInterceptor(Context context) {
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        String recupCookie = CookiePreferences.getStoredCookie(context);

//                PreferenceManager.getDefaultSharedPreferences(context)
//                .getString(PREF_COOKIES, null);

        if (recupCookie != null){
            builder.addHeader("Cookie", recupCookie);
            Log.i(TAG, "intercept: " + recupCookie);
        }
        Request r = builder.build();
        Log.i(TAG, "url : " +r.url().toString());
        return chain.proceed(r);

    }


}
