package com.soen490chrysalis.papilio.repository.genre

import android.util.Log
import com.soen490chrysalis.papilio.services.network.IGenreApiService
import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenreRepository(
    private val genreService : IGenreApiService,
    private val coroutineDispatcher : CoroutineDispatcher = Dispatchers.IO
) : IGenreRepository
{
    private val logTag = GenreRepository::class.java.simpleName

    override suspend fun getAllGenres() : Triple<Boolean, String, List<GenreObject>>
    {
        return withContext(coroutineDispatcher)
        {
            val response : Triple<Boolean, String, List<GenreObject>> = try
            {
                val result = genreService.getAllGenres()
                Log.d(logTag, "getAllGenres response $result")
                Triple(result.isSuccessful, result.message(), result.body()!!)
            }
            catch (e : Exception)
            {
                Log.d(logTag, "getAllGenres exception ${e.message.toString()}")
                Triple(false, e.message.toString(), listOf())
            }
            return@withContext response
        }
    }
}