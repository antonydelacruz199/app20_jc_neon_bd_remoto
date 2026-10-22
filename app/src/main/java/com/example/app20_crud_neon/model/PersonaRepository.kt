package com.example.app19_jc_neon_bd_remoto.model

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// Única capa que conoce Retrofit. Devuelve Result para que el ViewModel no maneje excepciones.
class PersonaRepository(
    private val api: PersonaApiService = RetrofitClient.api
) {

    suspend fun listar(q: String = ""): Result<List<Persona>> =
        ejecutar { api.listar(q.trim().ifEmpty { null }) }

    suspend fun insertar(persona: Persona): Result<Persona> = ejecutar { api.insertar(persona) }

    suspend fun actualizar(persona: Persona): Result<Persona> =
        ejecutar { api.actualizar(persona.id, persona) }

    suspend fun eliminar(id: Int): Result<Unit> = ejecutar {
        val respuesta = api.eliminar(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }

    private suspend fun <T> ejecutar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDelServidor(e)))
        } catch (e: IOException) {
            Result.failure(Exception("No se pudo conectar con el servidor"))
        }

    // La API responde errores como {"mensaje": "..."}; se muestra ese texto al usuario
    private fun mensajeDelServidor(e: HttpException): String {
        val cuerpo = e.response()?.errorBody()?.string()
        return try {
            JSONObject(cuerpo ?: "").getString("mensaje")
        } catch (ex: Exception) {
            "Error ${e.code()} del servidor"
        }
    }
}