package com.soen490chrysalis.papilio.repository.genre

import com.soen490chrysalis.papilio.services.network.responses.GenreObject

interface IGenreRepository
{
    suspend fun getAllGenres() : Triple<Boolean, String, List<GenreObject>>
}