package com.example.application080719;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface GetDataService {

    @Streaming
    @GET("uc")
    Call<ResponseBody> getAudioById(@Query("export") String query, @Query("id") String itemId);

}
