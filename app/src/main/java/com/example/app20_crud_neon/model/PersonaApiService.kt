package com.example.app19_jc_neon_bd_remoto.model

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonaApiService {

    @GET("api/personas")
    suspend fun listar(@Query("q") q: String? = null): List<Persona>

    @POST("api/personas")
    suspend fun insertar(@Body persona: Persona): Persona

    @PUT("api/personas/{id}")
    suspend fun actualizar(@Path("id") id: Int, @Body persona: Persona): Persona

    // La API responde 204 sin cuerpo, por eso Response<Unit>
    @DELETE("api/personas/{id}")
    suspend fun eliminar(@Path("id") id: Int): Response<Unit>
}