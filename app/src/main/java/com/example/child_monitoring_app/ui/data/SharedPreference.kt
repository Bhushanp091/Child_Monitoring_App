package com.example.child_monitoring_app.ui.data

import android.content.Context




object SharedPreference{
    fun saveParentIdLocally(context: Context, parentId: String) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("parent_id", parentId).apply()
    }

    fun getParentId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("parent_id", null)
    }

    fun saveChildIdLocally(context: Context, parentId: String) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("child_id", parentId).apply()
    }

    fun getChildId(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("child_id", null)
    }


}