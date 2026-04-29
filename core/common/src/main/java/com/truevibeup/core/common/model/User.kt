package com.truevibeup.core.common.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String = "",
    val name: String = "",
    val gender: String = "other",
    val birthday: String? = null,
    val age: Int? = null,
    val country: String? = null,
    val city: String? = null,
    val description: String? = null,
    val avatar: String? = null,
    val photos: List<String>? = emptyList(),

    @SerializedName("looking_for")
    val lookingFor: List<String>? = emptyList(),

    val traits: List<String>? = emptyList(),
    val hobbies: List<String>? = emptyList(),
    val movies: List<String>? = emptyList(),
    val music: List<String>? = emptyList(),
    val language: String = "en",

    @SerializedName("looking_for_age_min")
    val lookingForAgeMin: Int? = null,

    @SerializedName("looking_for_age_max")
    val lookingForAgeMax: Int? = null,

    @SerializedName("looking_for_gender")
    val lookingForGender: String? = null,

    @SerializedName("looking_for_personality")
    val lookingForPersonality: String? = null,

    // Các trường CamelCase từ response
    val isOnline: Int = 0,
    val lastSeenAt: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val createdAt: String? = null,
)

data class AuthTokens(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
)

data class Badges(
    @SerializedName("unread_messages") val unreadMessages: Int = 0,
    @SerializedName("unread_notifications") val unreadNotifications: Int = 0,
)

data class Country(
    val code: String? = null,
    @SerializedName("text") val name: String? = null
)

data class City(
    @SerializedName("geoname_id") val geonameId: Int? = null,
    @SerializedName("city_name") val name: String? = null,
    @SerializedName("subdivision_1_iso_code") val subdivisionCode: String? = null
)
