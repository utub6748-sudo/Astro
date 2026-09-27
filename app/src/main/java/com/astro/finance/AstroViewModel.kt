package com.astro.finance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AstroViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AstroRepository(app)
    private val _data = MutableStateFlow(repo.load())
    val data: StateFlow<AppData> = _data.asStateFlow()

    private fun update(block: (AppData) -> AppData) {
        val next = block(_data.value)
        _data.value = next
        repo.save(next)
    }

    fun addTransaction(t: TransactionItem) = update { it.copy(transactions = listOf(t) + it.transactions) }
    fun deleteTransaction(id: Long) = update { it.copy(transactions = it.transactions.filterNot { t -> t.id == id }) }
    fun addCategory(name: String) = update { d -> d.copy(categories = d.categories + CategoryItem(System.currentTimeMillis(), name)) }
    fun addHabit(h: HabitItem) = update { it.copy(habits = it.habits + h) }
    fun toggleHabit(id: Long, dateKey: String) = update { d ->
        val old = d.habitLogs.firstOrNull { it.habitId == id && it.dateKey == dateKey }
        val logs = if (old == null) d.habitLogs + HabitLog(id, dateKey, true)
        else d.habitLogs.map { if (it === old) it.copy(done = !it.done) else it }
        d.copy(habitLogs = logs)
    }
    fun addGoal(g: GoalItem) = update { it.copy(goals = it.goals + g) }
    fun addToGoal(id: Long, amount: Double) = update { d -> d.copy(goals = d.goals.map { if (it.id == id) it.copy(current = it.current + amount) else it }) }
    fun addTask(t: TaskItem) = update { it.copy(tasks = it.tasks + t) }
    fun toggleTask(id: Long) = update { d -> d.copy(tasks = d.tasks.map { if (it.id == id) it.copy(done = !it.done) else it }) }
    fun deleteTask(id: Long) = update { it.copy(tasks = it.tasks.filterNot { t -> t.id == id }) }
    fun updateSettings(s: AppSettings) = update { it.copy(settings = s) }
    fun replaceAll(d: AppData) { _data.value = d; repo.save(d) }
}
