 package com.example.gametrack.screens

import okhttp3.*
import com.google.gson.*
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.os.Handler
import android.os.Looper

object IGDBService {
    private const val CLIENT_ID = "2hvjsfjp9yx696o3mv28bl2c7j9xiy"
    private const val ACCESS_TOKEN = "bu6wlbtbkz04ddj1kam5k4eyb9o137"
    private const val BASE_URL = "https://api.igdb.com/v4/games"

    private val client = OkHttpClient()
    private val gson = Gson()

    fun buscarCaratula(nombreJuego: String, callback: (String?) -> Unit) {
        val requestBody = "search \"$nombreJuego\"; fields name, cover.image_id; limit 1;"
        val request = Request.Builder()
            .url(BASE_URL)
            .addHeader("Client-ID", CLIENT_ID)
            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
            .post(requestBody.toRequestBody("text/plain".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post { callback(null) }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        if (!it.isSuccessful) {
                            Handler(Looper.getMainLooper()).post { callback(null) }
                            return
                        }

                        val jsonArray = gson.fromJson(it.body?.string(), JsonArray::class.java)
                        if (jsonArray.size() > 0) {
                            val game = jsonArray[0].asJsonObject
                            val coverId = try {
                                game["cover"]?.asJsonObject?.get("image_id")?.asString
                            } catch (e: Exception) {
                                null
                            }

                            val imageUrl = coverId?.let { id ->
                                "https://images.igdb.com/igdb/image/upload/t_cover_big/$id.jpg"
                            }

                            Handler(Looper.getMainLooper()).post { callback(imageUrl) }
                        } else {
                            Handler(Looper.getMainLooper()).post { callback(null) }
                        }
                    } catch (e: Exception) {
                        Handler(Looper.getMainLooper()).post { callback(null) }
                    }
                }
            }
        })
    }
}
