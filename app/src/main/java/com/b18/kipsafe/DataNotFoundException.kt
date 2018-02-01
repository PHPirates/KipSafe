package com.b18.kipsafe

/**
 * Exception when data is not found, for example in shared preferences.
 */

class DataNotFoundException(message: String) : Exception(message)