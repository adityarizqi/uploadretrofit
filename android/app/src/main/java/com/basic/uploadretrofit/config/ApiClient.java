package com.basic.uploadretrofit.config;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    public static Retrofit retrofit = null;
    public static Retrofit getRetrofitInstance() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://172.25.112.1/phpnative/")
                    .client(new OkHttpClient())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
