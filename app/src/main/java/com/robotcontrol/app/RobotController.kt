package com.robotcontrol.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException

class RobotController {
    private val client = OkHttpClient()
    private val baseUrl = "http://192.168.4.1" // عنوان ESP32 الافتراضي

    suspend fun start() {
        makeRequest("/start")
    }

    suspend fun stop() {
        makeRequest("/stop")
    }

    suspend fun setMode(mode: String) {
        makeRequest("/$mode")
    }

    suspend fun setSpeed(speed: Int) {
        makePostRequest("/speed", "value=$speed")
    }

    suspend fun move(direction: String) {
        makePostRequest("/move", "direction=$direction")
    }

    private suspend fun makeRequest(endpoint: String) = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .build()
            client.newCall(request).execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private suspend fun makePostRequest(endpoint: String, body: String) = withContext(Dispatchers.IO) {
        try {
            val requestBody = RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded"),
                body
            )
            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .post(requestBody)
                .build()
            client.newCall(request).execute()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
