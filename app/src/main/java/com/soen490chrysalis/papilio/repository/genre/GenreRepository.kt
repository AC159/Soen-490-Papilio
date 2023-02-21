package com.soen490chrysalis.papilio.repository.genre

import android.util.Log
import com.soen490chrysalis.papilio.services.network.IGenreApiService
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
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
        category : String?
    ) : Response<List<GenreObject>>
    {
        return withContext(coroutineDispatcher)
        {
            val genres = genreService.getAllGenres(category)
            Log.d("getAllGenres_GenreRepository", genres.body().toString())
            return@withContext genres
        }
    }
}