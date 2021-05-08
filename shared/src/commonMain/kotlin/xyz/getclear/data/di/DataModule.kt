package xyz.getclear.data.di

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import xyz.getclear.android.data.BASE_URL
import xyz.getclear.data.net.*
import xyz.getclear.data.net.contract.DataRepository

const val BASE_URL_NAME = "base_url"

val dataModule = module {
    single(named(BASE_URL_NAME)) { BASE_URL }
    factory {
        HttpClient(get()) {
            install(JsonFeature) {
                install(HttpRedirect) {
                    checkHttpMethod = false
                    followRedirects = true
                }
                serializer = KotlinxSerializer(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowSpecialFloatingPointValues = true
                    useArrayPolymorphism = false
                })
            }
        }
    }
    single<DataRepository> { DataRepositoryImpl(get(), get(), get()) }
    single { DataLocalRepository() }


    single { UserRepository(get(), get(), get()) }
    single { CurrencyRepository(get(), get(), get()) }

    single { UserApi(get(named(BASE_URL_NAME)), get()) }
    single { PotApi(get(named(BASE_URL_NAME)), get(), get()) }
    single { TransactionApi(get(named(BASE_URL_NAME)), get(), get()) }
    single { CurrencyApi(get(named(BASE_URL_NAME)), get()) }
}