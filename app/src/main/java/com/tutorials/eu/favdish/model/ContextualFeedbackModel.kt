package com.tutorials.eu.favdish.model

data class ContextualFeedbackModel(
    val c: List<String>,
    val i: Int,
    val message: String,
    val title: String
)