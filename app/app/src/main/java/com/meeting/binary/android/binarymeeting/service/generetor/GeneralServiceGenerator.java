package com.meeting.binary.android.binarymeeting.service.generetor;

import android.content.Context;

import com.meeting.binary.android.binarymeeting.service.interceptor.AddCookiesInterceptor;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by loyck-daryl on 2018/3/12.
 */

public class GeneralServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    /**
     * build retrofit intance
     */
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BaseUrlGenerator.BINARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();


    public static <S> S CreateService(Class<S> serviceClass, Context context){
        List<Interceptor> in = httpClient.interceptors();
        boolean addInterceptor = true;
        for(Interceptor i: in){
            if(i instanceof AddCookiesInterceptor){
                addInterceptor = false;
                break;
            }
        }
        if(addInterceptor) {
            httpClient.addInterceptor(new AddCookiesInterceptor(context));
        }
        OkHttpClient client = httpClient.build();
        builder.client(client);
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
