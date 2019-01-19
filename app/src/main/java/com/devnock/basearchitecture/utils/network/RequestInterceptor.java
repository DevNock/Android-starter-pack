package com.devnock.basearchitecture.utils.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {

    public RequestInterceptor() {
        super();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}

