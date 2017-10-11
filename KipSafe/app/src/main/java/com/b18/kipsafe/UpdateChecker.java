package com.b18.kipsafe;

import android.app.Activity;

import com.winsontan520.wversionmanager.library.WVersionManager;

/**
 * Check for updates.
 */

public class UpdateChecker {

    private Activity activity;

    public UpdateChecker(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check for updates using WVersionManager.
     */
    public void check() {
        //check for updates (using library)
        WVersionManager versionManager = new WVersionManager(activity);
        versionManager.setVersionContentUrl("https://github.com/PHPirates/KipSafe/raw/master/version.json");
        versionManager.setUpdateUrl("https://github.com/PHPirates/KipSafe/raw/master/Kipsafe/app/app-release.apk");
        versionManager.checkVersion();
    }

}
