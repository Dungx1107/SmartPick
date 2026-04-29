package com.example.smartpick.features.auth.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityResponse(
    @SerialName("username_taken") val usernameTaken: Boolean,
    @SerialName("email_taken") val emailTaken: Boolean
)
