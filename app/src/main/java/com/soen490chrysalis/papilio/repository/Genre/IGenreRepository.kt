package com.soen490chrysalis.papilio.repository.Genre

import com.soen490chrysalis.papilio.services.network.responses.GenreObjectResponse
import retrofit2.Response

interface IGenreRepository
{
    suspend fun getAllGenres(
        category: String?
    ) : Response<GenreObjectResponse>
}