package com.devnock.basearchitecture.utils.network;

import com.devnock.basearchitecture.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final long TIME_OUT_IN_SECONDS = 30;

    public <S> S createService(Class<S> serviceClass) {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.BASE_URL);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new RequestInterceptor());
        builder.readTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS);
        builder.connectTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS);
        retrofitBuilder.client(builder.build());

        return retrofitBuilder.build().create(serviceClass);
    }
}
