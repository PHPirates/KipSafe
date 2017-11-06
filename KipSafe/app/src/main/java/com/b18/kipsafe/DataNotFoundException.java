package com.b18.kipsafe;

/**
 * Exception when data is not found, for example in shared preferences.
 */

public class DataNotFoundException extends Exception {

    public DataNotFoundException(String message) {
        super(message);
    }
}
