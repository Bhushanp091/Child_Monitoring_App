package com.example.child_monitoring_app.core.preference

import android.content.Context


object SharedPreference {
    fun saveParentIdLocally(context: Context, parentId: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_PARENT_ID, parentId).apply()
    }

    fun getParentId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_PARENT_ID, null)
    }

    fun saveChildIdLocally(context: Context, parentId: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_CHILD_ID, parentId).apply()
    }

    fun getChildId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_CHILD_ID, null)
    }


    fun saveParentLoginState(context: Context, isLoggedIn: Boolean = false) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(PARENT_IS_LOGGED_IN, isLoggedIn).apply()
    }


    fun saveChildLoginState(context: Context, isLoggedIn: Boolean = false) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(CHILD_IS_LOGGED_IN, isLoggedIn).apply()
    }


    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val result = sharedPref.getBoolean(PARENT_IS_LOGGED_IN, false)
        return result
    }


    fun isChildLoggedIn(context: Context):Boolean{
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(CHILD_IS_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }

    fun saveBlockedApps(context: Context, apps: List<String>) {
        val prefs = context.getSharedPreferences(BLOCKED_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(BLOCKED_PREFS, apps.toSet()).apply()
    }

    fun saveBlockedWeb(context: Context, webs: List<String>) {
        val prefs = context.getSharedPreferences(BLOCKED_WEB_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(BLOCKED_WEB_PREFS, webs.toSet()).apply()
    }

    private fun getBlockedApps(context: Context): Set<String> {
        val prefs = context.getSharedPreferences("blocker_prefs", Context.MODE_PRIVATE)
        return prefs.getStringSet("blocker_prefs", emptySet()) ?: emptySet()
    }



    private const val PREF_NAME = "child_monitor_pref"
    private const val KEY_PARENT_ID = "parentId"
    private const val KEY_CHILD_ID = "child_id"
    private const val PARENT_IS_LOGGED_IN = "is_parent_loggedIn"
    private const val CHILD_IS_LOGGED_IN = "is_child_loggedIn"
    private const val BLOCKED_PREFS = "blocker_prefs"
    private const val BLOCKED_WEB_PREFS = "web_prefs"

}