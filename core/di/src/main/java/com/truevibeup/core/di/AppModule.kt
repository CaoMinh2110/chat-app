package com.truevibeup.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.truevibeup.core.common.util.HtmlDecoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(String::class.java, object : TypeAdapter<String>() {
            override fun write(out: JsonWriter?, value: String?) {
                out?.value(value)
            }

            override fun read(`in`: JsonReader?): String? {
                // Check if the next token is a string, if not (null), skip it
                if (`in`?.peek() == com.google.gson.stream.JsonToken.NULL) {
                    `in`.nextNull()
                    return null
                }
                val value = `in`?.nextString()
                return HtmlDecoder.decode(value)
            }
        })
        .serializeNulls()
        .create()
}
