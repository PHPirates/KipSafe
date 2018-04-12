package com.b18.kipsafe.util

// Vibration sequence.
val kukeleku = longArrayOf(0, 200, 100, 50, 50, 50, 50, 1000, 1000)

// Preference keystrings. todo remove
@Deprecated("use spf")
const val prefTime = "preferencetime"
const val prefSunset = "preferencesunset"
const val prefIsAlarmSet = "preferenceisalarmset"

// URLs.
const val versionURL = "https://github.com/PHPirates/KipSafe/raw/master/version.json"
const val releaseURL =
        "https://github.com/PHPirates/KipSafe/blob/master/app/release/app-release.apk?raw=true"