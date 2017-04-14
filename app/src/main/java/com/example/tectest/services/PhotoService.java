package com.example.tectest.services;

import com.example.tectest.data.PhotoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ymontero on 14/04/2017.
 */

public interface PhotoService {
    //TODO change url for insert Geo Param
    @GET("users/{user}/repos")
    Call<PhotoList> getPhotoAround(@Path("user") String user);
}