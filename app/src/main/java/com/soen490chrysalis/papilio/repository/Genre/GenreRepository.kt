package com.soen490chrysalis.papilio.repository.Genre

import android.util.Log
import com.soen490chrysalis.papilio.services.network.IGenreApiService
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import com.soen490chrysalis.papilio.services.network.responses.GenreObjectResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class GenreRepository(
    private val genreService : IGenreApiService,
    private val coroutineDispatcher : CoroutineDispatcher = Dispatchers.IO
) : IGenreRepository
{
    override suspend fun getAllGenres(
        category: String?
    ) : Response<List<GenreObject>>
    {
        return withContext(coroutineDispatcher)
        {
            val whatever = genreService.getAllGenres(category)
            Log.d("getAllGenres_GenreRepository", whatever.body().toString())
            return@withContext genreService.getAllGenres(category)
        }
    }

}