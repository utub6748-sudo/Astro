package com.astro.finance

import android.content.Context

class AstroRepository(context: Context) {
    private val prefs = context.getSharedPreferences("astro_data", Context.MODE_PRIVATE)

    fun load(): AppData = runCatching {
        prefs.getString("payload", null)?.let(::appDataFromJson) ?: AppData()
    }.getOrElse { AppData() }

    fun save(data: AppData) {
        prefs.edit().putString("payload", data.toJson()).apply()
    }
}
