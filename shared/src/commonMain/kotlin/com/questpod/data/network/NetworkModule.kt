package com.questpod.data.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Network module for configuring HTTP clients for various services.
 */
object NetworkModule {
    /**
     * Creates a Ktor HttpClient configured for Supabase API.
     *
     * @param apiKey The Supabase API key
     * @param isDebug Whether to enable debug logging
     * @return Configured HttpClient instance
     */
    fun createSupabaseClient(apiKey: String, isDebug: Boolean = false): HttpClient {
        return createBaseClient(isDebug) {
            defaultRequest {
                headers.append("apikey", apiKey)
                headers.append("Content-Type", "application/json")
            }
        }
    }

    /**
     * Creates a Ktor HttpClient configured for a0.dev/Manus API for story generation.
     *
     * @param apiKey The a0.dev API key
     * @param isDebug Whether to enable debug logging
     * @return Configured HttpClient instance
     */
    fun createStoryGenerationClient(apiKey: String, isDebug: Boolean = false): HttpClient {
        return createBaseClient(isDebug) {
            defaultRequest {
                headers.append("Authorization", "Bearer $apiKey")
                headers.append("Content-Type", "application/json")
            }
        }
    }

    /**
     * Creates a Ktor HttpClient configured for ElevenLabs API for voice synthesis.
     *
     * @param apiKey The ElevenLabs API key
     * @param isDebug Whether to enable debug logging
     * @return Configured HttpClient instance
     */
    fun createVoiceSynthesisClient(apiKey: String, isDebug: Boolean = false): HttpClient {
        return createBaseClient(isDebug) {
            defaultRequest {
                headers.append("xi-api-key", apiKey)
                headers.append("Content-Type", "application/json")
            }
        }
    }

    /**
     * Creates a base HttpClient with common configuration.
     *
     * @param isDebug Whether to enable debug logging
     * @param block Additional configuration block
     * @return Configured HttpClient instance
     */
    private fun createBaseClient(isDebug: Boolean = false, block: HttpClientConfig<*>.() -> Unit = {}): HttpClient {
        return HttpClient {
            // Configure JSON serialization
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // Configure timeout
            install(HttpTimeout) {
                requestTimeoutMillis = 30000 // 30 seconds
                connectTimeoutMillis = 15000 // 15 seconds
                socketTimeoutMillis = 60000  // 60 seconds
            }

            // Configure logging for debug builds
            if (isDebug) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = Logger.DEFAULT
                }
            }

            // Apply additional configuration
            block()
        }
    }
}
