package com.example.films.data

data class Response(
    var id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val description: String? = null,
    var year: String? = "1900",
    var producer: String? = null,
    var isLiked: Boolean? = null
)
