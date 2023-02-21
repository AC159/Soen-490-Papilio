package com.soen490chrysalis.papilio.repository.genre

import com.soen490chrysalis.papilio.services.network.responses.GenreObject
import retrofit2.Response

interface IGenreRepository
{
    suspend fun getAllGenres(
        category: String?
    ) : Response<List<GenreObject>>
}