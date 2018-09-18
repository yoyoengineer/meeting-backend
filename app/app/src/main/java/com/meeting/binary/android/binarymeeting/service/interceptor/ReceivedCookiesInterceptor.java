package com.meeting.binary.android.binarymeeting.service.interceptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;


/**
 * Created by loyck-daryl on 2018/3/8.
 */

public class ReceivedCookiesInterceptor implements Interceptor {

    private Context context;
    private static final String TAG = "RecievedCookies";

    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        String headerString = originalResponse.header("Set-Cookie");
        String headerFormat;

        if (headerString != null) {
            headerFormat = headerString.split(";")[0];
            Log.i(TAG, "intercept: " + headerFormat.toString());
            CookiePreferences.setStoredCookie(context, headerFormat);
//            SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(context).edit();
//            memes.putString("PREF_COOKIES", headerFormat).apply();
//            memes.commit();
        }

        return originalResponse;
    }
}
