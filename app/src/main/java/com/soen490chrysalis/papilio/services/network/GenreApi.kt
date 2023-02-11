package com.soen490chrysalis.papilio.services.network

import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import com.soen490chrysalis.papilio.services.network.responses.GenreObjectResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

/*
    File that manages the network requests related to dynamically populating buttons in the user activity quiz
    and allowing the user to send their selections to the backend.

    Author: Zain Khan
    Date: January 29, 2022
 */

// Build the Moshi object with Kotlin adapter factory that Retrofit will be using
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

// The Retrofit object with the Moshi converter.
private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("http://localhost:1337/api/genre/")
        .build()

interface IGenreApiService
{
    @GET ("all")
    suspend fun getAllGenres(
        @Query("category") category : String?
    ) : Response<List<GenreObject>>
}

object GenreApi{
    // Create a singleton object that implements the UserApiService interface
    val retrofitService : IGenreApiService by lazy {
        retrofit.create(IGenreApiService::class.java)
    }
}