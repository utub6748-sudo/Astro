package com.astro.finance

import org.json.JSONArray
import org.json.JSONObject

private fun JSONObject.optLongCompat(key: String, default: Long = 0L): Long = if (has(key)) optLong(key, default) else default

 data class TransactionItem(
    val id: Long,
    val amount: Double,
    val currency: String,
    val rateToBase: Double,
    val category: String,
    val note: String,
    val date: Long,
    val type: String
)

data class CategoryItem(val id: Long, val name: String)
data class HabitItem(val id: Long, val name: String, val frequency: String, val createdAt: Long)
data class HabitLog(val habitId: Long, val dateKey: String, val done: Boolean)
data class GoalItem(
    val id: Long,
    val title: String,
    val target: Double,
    val current: Double,
    val currency: String,
    val dueDate: Long?,
    val note: String
)
data class TaskItem(
    val id: Long,
    val title: String,
    val description: String,
    val start: Long,
    val end: Long?,
    val done: Boolean
)
data class AppSettings(
    val baseCurrency: String = "EUR",
    val theme: String = "system",
    val colorMode: String = "gradient",
    val color1: Long = 0xFF7C3AED,
    val color2: Long = 0xFF06B6D4,
    val haptics: Boolean = true
)
data class AppData(
    val transactions: List<TransactionItem> = emptyList(),
    val categories: List<CategoryItem> = defaultCategories(),
    val habits: List<HabitItem> = emptyList(),
    val habitLogs: List<HabitLog> = emptyList(),
    val goals: List<GoalItem> = emptyList(),
    val tasks: List<TaskItem> = emptyList(),
    val settings: AppSettings = AppSettings()
)

fun defaultCategories() = listOf(
    CategoryItem(1, "Еда"), CategoryItem(2, "Дом"), CategoryItem(3, "Транспорт"),
    CategoryItem(4, "Покупки"), CategoryItem(5, "Здоровье"), CategoryItem(6, "Развлечения"),
    CategoryItem(7, "Подписки"), CategoryItem(8, "Зарплата"), CategoryItem(9, "Другое")
)

fun AppData.toJson(): String {
    val root = JSONObject()
    root.put("settings", JSONObject().apply {
        put("baseCurrency", settings.baseCurrency)
        put("theme", settings.theme)
        put("colorMode", settings.colorMode)
        put("color1", settings.color1)
        put("color2", settings.color2)
        put("haptics", settings.haptics)
    })
    root.put("transactions", JSONArray().apply {
        transactions.forEach { t -> put(JSONObject().apply {
            put("id", t.id); put("amount", t.amount); put("currency", t.currency); put("rate", t.rateToBase)
            put("category", t.category); put("note", t.note); put("date", t.date); put("type", t.type)
        }) }
    })
    root.put("categories", JSONArray().apply { categories.forEach { put(JSONObject().apply { put("id", it.id); put("name", it.name) }) } })
    root.put("habits", JSONArray().apply { habits.forEach { put(JSONObject().apply { put("id", it.id); put("name", it.name); put("frequency", it.frequency); put("createdAt", it.createdAt) }) } })
    root.put("habitLogs", JSONArray().apply { habitLogs.forEach { put(JSONObject().apply { put("habitId", it.habitId); put("dateKey", it.dateKey); put("done", it.done) }) } })
    root.put("goals", JSONArray().apply { goals.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("target", it.target); put("current", it.current); put("currency", it.currency); put("dueDate", it.dueDate); put("note", it.note) }) } })
    root.put("tasks", JSONArray().apply { tasks.forEach { put(JSONObject().apply { put("id", it.id); put("title", it.title); put("description", it.description); put("start", it.start); put("end", it.end); put("done", it.done) }) } })
    return root.toString()
}

fun appDataFromJson(text: String): AppData {
    val root = JSONObject(text)
    val s = root.optJSONObject("settings") ?: JSONObject()
    val settings = AppSettings(
        s.optString("baseCurrency", "EUR"), s.optString("theme", "system"), s.optString("colorMode", "gradient"),
        s.optLong("color1", 0xFF7C3AED), s.optLong("color2", 0xFF06B6D4), s.optBoolean("haptics", true)
    )
    fun <T> arr(name: String, mapper: (JSONObject) -> T): List<T> {
        val a = root.optJSONArray(name) ?: JSONArray()
        return buildList { for (i in 0 until a.length()) add(mapper(a.getJSONObject(i))) }
    }
    val tx = arr("transactions") { o -> TransactionItem(o.optLongCompat("id"), o.optDouble("amount"), o.optString("currency", "EUR"), o.optDouble("rate", 1.0), o.optString("category", "Другое"), o.optString("note"), o.optLongCompat("date"), o.optString("type", "expense")) }
    val cats = arr("categories") { o -> CategoryItem(o.optLongCompat("id"), o.optString("name")) }.ifEmpty { defaultCategories() }
    val habits = arr("habits") { o -> HabitItem(o.optLongCompat("id"), o.optString("name"), o.optString("frequency", "Ежедневно"), o.optLongCompat("createdAt")) }
    val logs = arr("habitLogs") { o -> HabitLog(o.optLongCompat("habitId"), o.optString("dateKey"), o.optBoolean("done")) }
    val goals = arr("goals") { o -> GoalItem(o.optLongCompat("id"), o.optString("title"), o.optDouble("target"), o.optDouble("current"), o.optString("currency", settings.baseCurrency), if (o.isNull("dueDate")) null else o.optLong("dueDate"), o.optString("note")) }
    val tasks = arr("tasks") { o -> TaskItem(o.optLongCompat("id"), o.optString("title"), o.optString("description"), o.optLongCompat("start"), if (o.isNull("end")) null else o.optLong("end"), o.optBoolean("done")) }
    return AppData(tx, cats, habits, logs, goals, tasks, settings)
}
