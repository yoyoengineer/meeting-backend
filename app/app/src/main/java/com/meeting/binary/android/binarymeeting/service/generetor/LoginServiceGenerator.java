package com.meeting.binary.android.binarymeeting.service.generetor;


import android.content.Context;
import android.text.TextUtils;

import com.meeting.binary.android.binarymeeting.service.interceptor.AddCookiesInterceptor;
import com.meeting.binary.android.binarymeeting.service.interceptor.AuthenticationInterceptor;
import com.meeting.binary.android.binarymeeting.service.interceptor.ReceivedCookiesInterceptor;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    /**
     * build retrofit intance
     */
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BaseUrlGenerator.BINARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit = builder.build();


    /**
     * called on login and generate the authentication credentials
     * @param serviceClass
     * @param email
     * @param password
     * @param context
     * @param <S>
     * @return
     */
    public static <S> S createService(Class<S> serviceClass, String email, String password,
                                      Context context){
        if (email!=null && password!=null && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(email, password);
            return createService(serviceClass, authToken, context);
        }

        return createService(serviceClass, null, context);
    }



    /**
     * handles the credentials, add interceptors and return the restful instance built
     * @param serviceClass
     * @param authToken
     * @param context
     * @param <S>
     * @return
     */
    public static <S> S createService(Class<S> serviceClass, final String authToken, Context context){

        if (authToken!=null && !TextUtils.isEmpty(authToken)){
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(interceptor)) {

                httpClient.addInterceptor(interceptor);
                //intercepts cookies interceptors in the client builder
                httpClient.addInterceptor(new ReceivedCookiesInterceptor(context));
                OkHttpClient client = httpClient.build();
                builder.client(client);
                retrofit = builder.build();
            }
        }
        return retrofit.create(serviceClass);
    }



    //test
    public static <S> S createService(Class<S> serviceClass, Context context){
        httpClient.addInterceptor(new AddCookiesInterceptor(context));
        OkHttpClient client = httpClient.build();
        builder.client(client);
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }

}
