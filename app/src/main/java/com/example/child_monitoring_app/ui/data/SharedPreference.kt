package com.example.child_monitoring_app.ui.data

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


    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }


    private const val PREF_NAME = "ChildMonitorPrefs"
    private const val KEY_PARENT_ID = "parentId"
    private const val KEY_CHILD_ID = "child_id"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"

}