package com.b18.kipsafe

import android.app.Activity
import com.winsontan520.wversionmanager.library.WVersionManager

/**
 * Checks for updates using WVersionManager.
 */
class UpdateChecker(private val activity: Activity) {
    fun check() {
        val versionManager = WVersionManager(activity)
        versionManager.versionContentUrl =
                activity.resources.getString(R.string.version_url)
        versionManager.updateUrl =
                activity.resources.getString(R.string.release_url)
        versionManager.checkVersion()
    }
}