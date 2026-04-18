package com.example.smartpick.core.model

data class Post(
    val id:String,
    val idUser:String,
    val content:String?=null,
    val createAt:String,
    val images:List<String> = emptyList(),

)
