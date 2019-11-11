package com.juaracoding.contohpost.APIService;

/**
 * Created by user on 1/10/2018.
 */





import com.juaracoding.contohpost.ModelAdd;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface APIInterfacesRest {

    @Multipart
    @POST("juara_textjalan/add")
    Call<ModelAdd> sendTextJalan(

            @Part("dari") RequestBody dari,
            @Part("time") RequestBody time,
            @Part("text") RequestBody text,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part picture1
    );

     

}

