package com.kmpstarter.core.ktor.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

val ktorModule = module {
    single {
        HttpClient {

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
            install(ContentNegotiation) {
                json()
            }
            install(HttpTimeout) {
                // configure according to need
                socketTimeoutMillis = 100 * 1000
                connectTimeoutMillis = 100 * 1000
                requestTimeoutMillis = 100 * 1000
            }

            defaultRequest {
                /*
                configure default request
                headers.append(HttpHeaders.ContentType, "application/json")
                */
            }

        }
    }
}