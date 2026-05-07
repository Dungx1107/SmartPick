package com.example.smartpick.core.utils

object Logger {

    private const val ENABLE_LOG = true

    fun d(tag: String, message: String) {
        if (ENABLE_LOG) {
            println("DEBUG: [$tag] $message")
        }
    }

    fun e(tag: String, message: String) {
        if (ENABLE_LOG) {
            System.err.println("ERROR: [$tag] $message")
        }
    }
}