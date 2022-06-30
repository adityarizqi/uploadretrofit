package com.basic.uploadretrofit.api;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Server {
    @Multipart
    @POST("upload_api.php")
    Call<String> uploadImage(@Part MultipartBody.Part part);
}
