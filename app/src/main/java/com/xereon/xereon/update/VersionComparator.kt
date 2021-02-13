package com.xereon.xereon.update

object VersionComparator {

    /**
     * Checks if currentVersion is older than versionToCompareTo
     *
     * Expected input format: <major>.<minor>.<patch>
     * major, minor and patch are Int
     *
     * @param currentVersion
     * @param versionToCompareTo
     * @return true if currentVersion is older than versionToCompareTo
     * @return false if one of the inputs is not valid
     */
    fun isVersionOlder(currentVersion: Int, versionToCompareTo: Int): Boolean {
        if (currentVersion == -1 || versionToCompareTo == -1)
            return false
        return currentVersion < versionToCompareTo
    }
}