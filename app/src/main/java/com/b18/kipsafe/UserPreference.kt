package com.b18.kipsafe

/**
 * Defines which user preferences are stored and can be retrieved using SharedPreferenceManager.
 */
enum class UserPreference(val keyString: String) {

    TIME("preferencetime"),
    SUNSET("preferencesunset"),
    IS_ALARM_SET("preferenceisalarmset"),
    WEEKEND_ONLY("preferenceweekendonly")

}