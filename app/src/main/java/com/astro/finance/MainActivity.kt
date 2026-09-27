package com.astro.finance

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val vm by viewModels<AstroViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AstroRoot(vm) }
    }
}

private enum class Screen { HOME, FINANCE, HABITS, GOALS, TASKS, SETTINGS }

@Composable
private fun AstroRoot(vm: AstroViewModel) {
    val data by vm.data.collectAsStateWithLifecycle()
    val darkSystem = androidx.compose.foundation.isSystemInDarkTheme()
    val dark = when (data.settings.theme) { "dark" -> true; "light" -> false; else -> darkSystem }
    val c1 = Color(data.settings.color1)
    val c2 = Color(data.settings.color2)
    val primary = if (data.settings.colorMode == "static") c1 else c1
    val scheme = if (dark) androidx.compose.material3.darkColorScheme(primary = primary, secondary = c2)
    else androidx.compose.material3.lightColorScheme(primary = primary, secondary = c2)
    androidx.compose.material3.MaterialTheme(colorScheme = scheme) {
        AstroScaffold(data, vm, dark, c1, c2)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AstroScaffold(data: AppData, vm: AstroViewModel, dark: Boolean, c1: Color, c2: Color) {
    var screen by remember { mutableStateOf(Screen.HOME) }
    val titles = mapOf(Screen.HOME to "Обзор", Screen.FINANCE to "Финансы", Screen.HABITS to "Привычки", Screen.GOALS to "Цели", Screen.TASKS to "Задачи", Screen.SETTINGS to "Настройки")
    val gradient = if (data.settings.colorMode == "gradient") Brush.linearGradient(listOf(c1, c2)) else Brush.linearGradient(listOf(c1, c1))
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Astro · ${titles[screen]}", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(modifier = Modifier.navigationBarsPadding()) {
                listOf(Screen.HOME, Screen.FINANCE, Screen.HABITS, Screen.GOALS, Screen.TASKS).forEach { s ->
                    val icon = when (s) {
                        Screen.HOME -> Icons.Default.Home
                        Screen.FINANCE -> Icons.Default.Wallet
                        Screen.HABITS -> Icons.Default.CheckCircle
                        Screen.GOALS -> Icons.Default.Flag
                        Screen.TASKS -> Icons.Default.TaskAlt
                        else -> Icons.Default.Home
                    }
                    NavigationBarItem(selected = screen == s, onClick = { screen = s }, icon = { Icon(icon, null) }, label = { Text(titles[s] ?: "") })
                }
                NavigationBarItem(selected = screen == Screen.SETTINGS, onClick = { screen = Screen.SETTINGS }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Ещё") })
            }
        },
        floatingActionButton = {
            if (screen != Screen.SETTINGS && screen != Screen.HOME) {
                FloatingActionButton(onClick = {
                    when (screen) {
                        Screen.FINANCE -> Unit
                        Screen.HABITS -> Unit
                        Screen.GOALS -> Unit
                        Screen.TASKS -> Unit
                        else -> Unit
                    }
                }, containerColor = c1) { Icon(Icons.Default.Add, "Добавить") }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                Screen.HOME -> HomeScreen(data, gradient) { screen = it }
                Screen.FINANCE -> FinanceScreen(data, vm, c1)
                Screen.HABITS -> HabitsScreen(data, vm, c1)
                Screen.GOALS -> GoalsScreen(data, vm, c1)
                Screen.TASKS -> TasksScreen(data, vm, c1)
                Screen.SETTINGS -> SettingsScreen(data, vm, c1, c2)
            }
        }
    }
}

private fun money(v: Double, currency: String): String = "${String.format(Locale.getDefault(), "%.2f", v)} $currency"
private fun dateText(millis: Long): String = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(millis))
private fun dateTimeText(millis: Long): String = SimpleDateFormat("dd.MM.yyyy · HH:mm", Locale.getDefault()).format(Date(millis))
private fun dayKey(millis: Long): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(millis))
private fun now() = System.currentTimeMillis()

@Composable
private fun GradientHeader(brush: Brush, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.clip(RoundedCornerShape(28.dp)).background(brush).padding(22.dp), content = content)
}

@Composable
private fun HomeScreen(data: AppData, gradient: Brush, open: (Screen) -> Unit) {
    val base = data.settings.baseCurrency
    val income = data.transactions.filter { it.type == "income" }.sumOf { it.amount * it.rateToBase }
    val expense = data.transactions.filter { it.type == "expense" }.sumOf { it.amount * it.rateToBase }
    val balance = income - expense
    val completedHabits = data.habitLogs.count { it.dateKey == dayKey(now()) && it.done }
    val openTasks = data.tasks.count { !it.done }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Spacer(Modifier.height(8.dp))
            GradientHeader(gradient, Modifier.fillMaxWidth()) {
                Text("Ваши финансы под контролем", color = Color.White.copy(alpha = .85f), fontSize = 13.sp)
                Spacer(Modifier.height(7.dp))
                Text(money(balance, base), color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("Текущий баланс в базовой валюте", color = Color.White.copy(alpha = .78f), fontSize = 12.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatPill("Доходы", money(income, base), Color.White.copy(alpha = .18f), Modifier.weight(1f))
                    StatPill("Расходы", money(expense, base), Color.White.copy(alpha = .18f), Modifier.weight(1f))
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MiniCard("Привычки", "$completedHabits/${data.habits.size}", "сегодня", Modifier.weight(1f), { open(Screen.HABITS) })
                MiniCard("Задачи", "$openTasks", "в работе", Modifier.weight(1f), { open(Screen.TASKS) })
            }
        }
        item {
            Text("Последние операции", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        if (data.transactions.isEmpty()) item { EmptyState("Операций пока нет", "Добавьте доход или расход в разделе «Финансы».") }
        items(data.transactions.take(8), key = { it.id }) { t -> TransactionRow(t, base, null) }
        item {
            Spacer(Modifier.height(8.dp))
            Text("Ваши цели", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        if (data.goals.isEmpty()) item { EmptyState("Цели не созданы", "Создайте финансовую цель и следите за прогрессом.") }
        items(data.goals.take(3), key = { it.id }) { g -> GoalRow(g, null) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatPill(title: String, value: String, bg: Color, modifier: Modifier = Modifier) {
    Column(modifier.background(bg, RoundedCornerShape(16.dp)).padding(12.dp)) {
        Text(title, color = Color.White.copy(alpha = .78f), fontSize = 11.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun MiniCard(title: String, value: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier.clickable(onClick = onClick), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(17.dp)) {
            Text(title, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Card(shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

@Composable
private fun FinanceScreen(data: AppData, vm: AstroViewModel, accent: Color) {
    var dialog by remember { mutableStateOf(false) }
    var categoryDialog by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("all") }
    val base = data.settings.baseCurrency
    val income = data.transactions.filter { it.type == "income" }.sumOf { it.amount * it.rateToBase }
    val expense = data.transactions.filter { it.type == "expense" }.sumOf { it.amount * it.rateToBase }
    val filtered = data.transactions.filter { filter == "all" || it.type == filter }
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryCard("Доходы", money(income, base), Modifier.weight(1f))
                    SummaryCard("Расходы", money(expense, base), Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    FilterChip("Все", filter == "all") { filter = "all" }
                    FilterChip("Расходы", filter == "expense") { filter = "expense" }
                    FilterChip("Доходы", filter == "income") { filter = "income" }
                    FilterChip("Категории", false) { categoryDialog = true }
                }
            }
            item { Text("Операции", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            if (filtered.isEmpty()) item { EmptyState("Нет операций", "Нажмите +, чтобы добавить первую запись.") }
            items(filtered, key = { it.id }) { t -> TransactionRow(t, base) { vm.deleteTransaction(t.id) } }
            item { Spacer(Modifier.height(80.dp)) }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { dialog = true }, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Операция") }
            OutlinedButton(onClick = { categoryDialog = true }) { Icon(Icons.Default.Tune, null); Spacer(Modifier.width(6.dp)); Text("Группы") }
        }
    }
    if (dialog) AddTransactionDialog(data, vm, accent) { dialog = false }
    if (categoryDialog) CategoryDialog(data, vm) { categoryDialog = false }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(16.dp)) { Text(title, fontSize = 12.sp); Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp) } }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(50), color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant) {
        Text(text, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp), fontSize = 13.sp)
    }
}

@Composable
private fun TransactionRow(t: TransactionItem, base: String, onDelete: (() -> Unit)?) {
    Card(shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(if (t.type == "income") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer), contentAlignment = Alignment.Center) {
                Icon(if (t.type == "income") Icons.Default.AttachMoney else Icons.Default.MoreTime, null)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.category, fontWeight = FontWeight.SemiBold)
                Text("${dateText(t.date)}${if (t.note.isBlank()) "" else " · ${t.note}"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(if (t.type == "income") "+${money(t.amount, t.currency)}" else "−${money(t.amount, t.currency)}", fontWeight = FontWeight.Bold)
                if (t.currency != base) Text("≈ ${money(t.amount * t.rateToBase, base)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onDelete != null) IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Удалить") }
        }
    }
}

@Composable
private fun AddTransactionDialog(data: AppData, vm: AstroViewModel, accent: Color, close: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf(data.settings.baseCurrency) }
    var rate by remember { mutableStateOf("1") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var category by remember { mutableStateOf(data.categories.firstOrNull()?.name ?: "Другое") }
    AlertDialog(onDismissRequest = close, title = { Text("Новая операция") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip("Расход", type == "expense") { type = "expense" }; FilterChip("Доход", type == "income") { type = "income" } }
            OutlinedTextField(amount, { amount = it }, label = { Text("Сумма") }, singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(currency, { currency = it.uppercase() }, label = { Text("Валюта") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(rate, { rate = it }, label = { Text("Курс к ${data.settings.baseCurrency}") }, modifier = Modifier.weight(1.2f), singleLine = true)
            }
            DropdownField("Группа", category, data.categories.map { it.name }) { category = it }
            OutlinedTextField(note, { note = it }, label = { Text("Комментарий") }, singleLine = true)
            Text("Для другой валюты укажите курс 1 единицы валюты к базовой. Расчёты выполняются автоматически.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }, confirmButton = {
        Button(onClick = {
            val a = amount.replace(',', '.').toDoubleOrNull() ?: return@Button
            val r = rate.replace(',', '.').toDoubleOrNull() ?: 1.0
            vm.addTransaction(TransactionItem(now(), a, currency.ifBlank { data.settings.baseCurrency }, r, category, note, now(), type)); close()
        }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = accent)) { Text("Сохранить") }
    }, dismissButton = { TextButton(onClick = close) { Text("Отмена") } })
}

@Composable
private fun CategoryDialog(data: AppData, vm: AstroViewModel, close: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = close, title = { Text("Группы трат") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.categories.forEach { Text("• ${it.name}") }
            Divider()
            OutlinedTextField(name, { name = it }, label = { Text("Новая группа") }, singleLine = true)
        }
    }, confirmButton = { Button(onClick = { if (name.isNotBlank()) { vm.addCategory(name.trim()); close() } }) { Text("Добавить") } }, dismissButton = { TextButton(onClick = close) { Text("Закрыть") } })
}

@Composable
private fun DropdownField(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(value, {}, label = { Text(label) }, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { expanded = true })
        DropdownMenu(expanded, { expanded = false }) { options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false }) } }
    }
}

@Composable
private fun HabitsScreen(data: AppData, vm: AstroViewModel, accent: Color) {
    var dialog by remember { mutableStateOf(false) }
    val today = dayKey(now())
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("Сегодня · ${dateText(now())}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (data.habits.isEmpty()) item { EmptyState("Привычек пока нет", "Создайте привычку и отмечайте выполнение каждый день.") }
            items(data.habits, key = { it.id }) { h ->
                val done = data.habitLogs.firstOrNull { it.habitId == h.id && it.dateKey == today }?.done == true
                Card(shape = RoundedCornerShape(20.dp)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(done, { vm.toggleHabit(h.id, today) })
                        Column(Modifier.weight(1f)) { Text(h.name, fontWeight = FontWeight.SemiBold); Text(h.frequency, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        if (done) Icon(Icons.Default.Check, null, tint = accent)
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
        Button(onClick = { dialog = true }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Добавить привычку") }
    }
    if (dialog) AddHabitDialog(vm) { dialog = false }
}

@Composable
private fun AddHabitDialog(vm: AstroViewModel, close: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Ежедневно") }
    AlertDialog(onDismissRequest = close, title = { Text("Новая привычка") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(name, { name = it }, label = { Text("Название") }); DropdownField("Частота", frequency, listOf("Ежедневно", "По будням", "Еженедельно")) { frequency = it } } }, confirmButton = { Button(onClick = { if (name.isNotBlank()) { vm.addHabit(HabitItem(now(), name.trim(), frequency, now())); close() } }) { Text("Создать") } }, dismissButton = { TextButton(onClick = close) { Text("Отмена") } })
}

@Composable
private fun GoalsScreen(data: AppData, vm: AstroViewModel, accent: Color) {
    var dialog by remember { mutableStateOf(false) }
    var addDialog by remember { mutableStateOf<GoalItem?>(null) }
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("Финансовые цели", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            if (data.goals.isEmpty()) item { EmptyState("Целей пока нет", "Например: подушка безопасности, отпуск, техника или автомобиль.") }
            items(data.goals, key = { it.id }) { g -> GoalRow(g) { addDialog = g } }
            item { Spacer(Modifier.height(80.dp)) }
        }
        Button(onClick = { dialog = true }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Добавить цель") }
    }
    if (dialog) AddGoalDialog(data, vm) { dialog = false }
    addDialog?.let { g -> AddGoalProgressDialog(g, vm) { addDialog = null } }
}

@Composable
private fun GoalRow(g: GoalItem, onAdd: (() -> Unit)?) {
    val p = if (g.target <= 0) 0f else (g.current / g.target).coerceIn(0.0, 1.0).toFloat()
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text(g.title, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f)); Text("${(p * 100).toInt()}%", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { p }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape))
            Spacer(Modifier.height(7.dp)); Text("${money(g.current, g.currency)} из ${money(g.target, g.currency)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (g.note.isNotBlank()) Text(g.note, fontSize = 12.sp)
            if (onAdd != null) { Spacer(Modifier.height(9.dp)); OutlinedButton(onClick = onAdd) { Text("Добавить прогресс") } }
        }
    }
}

@Composable
private fun AddGoalDialog(data: AppData, vm: AstroViewModel, close: () -> Unit) {
    var title by remember { mutableStateOf("") }; var target by remember { mutableStateOf("") }; var currency by remember { mutableStateOf(data.settings.baseCurrency) }; var note by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = close, title = { Text("Новая цель") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(title, { title = it }, label = { Text("Название") }); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(target, { target = it }, label = { Text("Сумма") }, modifier = Modifier.weight(1f)); OutlinedTextField(currency, { currency = it.uppercase() }, label = { Text("Валюта") }, modifier = Modifier.weight(1f)) }; OutlinedTextField(note, { note = it }, label = { Text("Комментарий") }) } }, confirmButton = { Button(onClick = { val t = target.replace(',', '.').toDoubleOrNull() ?: return@Button; if (title.isNotBlank()) { vm.addGoal(GoalItem(now(), title.trim(), t, 0.0, currency, null, note)); close() } }) { Text("Создать") } }, dismissButton = { TextButton(onClick = close) { Text("Отмена") } })
}

@Composable
private fun AddGoalProgressDialog(g: GoalItem, vm: AstroViewModel, close: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = close, title = { Text("Прогресс: ${g.title}") }, text = { OutlinedTextField(amount, { amount = it }, label = { Text("Добавить ${g.currency}") }) }, confirmButton = { Button(onClick = { amount.replace(',', '.').toDoubleOrNull()?.let { vm.addToGoal(g.id, it); close() } }) { Text("Добавить") } }, dismissButton = { TextButton(onClick = close) { Text("Отмена") } })
}

@Composable
private fun TasksScreen(data: AppData, vm: AstroViewModel, accent: Color) {
    var dialog by remember { mutableStateOf(false) }
    val sorted = data.tasks.sortedWith(compareBy<TaskItem> { it.done }.thenBy { it.start })
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("План и задачи", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            if (sorted.isEmpty()) item { EmptyState("Задач пока нет", "Добавляйте задачи на конкретную дату, время или интервал.") }
            items(sorted, key = { it.id }) { t ->
                Card(shape = RoundedCornerShape(20.dp)) {
                    Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(t.done, { vm.toggleTask(t.id) })
                        Column(Modifier.weight(1f)) {
                            Text(t.title, fontWeight = FontWeight.SemiBold)
                            Text(dateTimeText(t.start) + (t.end?.let { " — ${dateTimeText(it)}" } ?: ""), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (t.description.isNotBlank()) Text(t.description, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = { vm.deleteTask(t.id) }) { Icon(Icons.Default.Delete, "Удалить") }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
        Button(onClick = { dialog = true }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Добавить задачу") }
    }
    if (dialog) AddTaskDialog(vm) { dialog = false }
}

@Composable
private fun AddTaskDialog(vm: AstroViewModel, close: () -> Unit) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }; var desc by remember { mutableStateOf("") }; var start by remember { mutableStateOf(now()) }; var end by remember { mutableStateOf<Long?>(null) }
    fun pickDate(current: Long, callback: (Long) -> Unit) {
        val c = Calendar.getInstance().apply { timeInMillis = current }
        DatePickerDialog(context, { _, y, m, d -> Calendar.getInstance().apply { timeInMillis = current; set(y, m, d); callback(timeInMillis) } }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }
    fun pickTime(current: Long, callback: (Long) -> Unit) {
        val c = Calendar.getInstance().apply { timeInMillis = current }
        TimePickerDialog(context, { _, h, min -> Calendar.getInstance().apply { timeInMillis = current; set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, min); callback(timeInMillis) } }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }
    AlertDialog(onDismissRequest = close, title = { Text("Новая задача") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(title, { title = it }, label = { Text("Название") }, singleLine = true)
        OutlinedTextField(desc, { desc = it }, label = { Text("Описание") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton(onClick = { pickDate(start) { start = it } }, modifier = Modifier.weight(1f)) { Text(dateText(start)) }; OutlinedButton(onClick = { pickTime(start) { start = it } }, modifier = Modifier.weight(1f)) { Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(start))) } }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton(onClick = { val base = end ?: start; pickDate(base) { end = it } }, modifier = Modifier.weight(1f)) { Text(end?.let(::dateText) ?: "Дата конца") }; OutlinedButton(onClick = { val base = end ?: start; pickTime(base) { end = it } }, modifier = Modifier.weight(1f)) { Text(end?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)) } ?: "Время конца") } }
        if (end != null) TextButton(onClick = { end = null }) { Text("Убрать конечное время") }
    } }, confirmButton = { Button(onClick = { if (title.isNotBlank()) { vm.addTask(TaskItem(now(), title.trim(), desc.trim(), start, end, false)); close() } }) { Text("Создать") } }, dismissButton = { TextButton(onClick = close) { Text("Отмена") } })
}

@Composable
private fun SettingsScreen(data: AppData, vm: AstroViewModel, c1: Color, c2: Color) {
    val context = LocalContext.current
    var base by remember(data.settings.baseCurrency) { mutableStateOf(data.settings.baseCurrency) }
    var theme by remember(data.settings.theme) { mutableStateOf(data.settings.theme) }
    var mode by remember(data.settings.colorMode) { mutableStateOf(data.settings.colorMode) }
    var color1 by remember(data.settings.color1) { mutableStateOf(data.settings.color1) }
    var color2 by remember(data.settings.color2) { mutableStateOf(data.settings.color2) }
    var exportJson by remember { mutableStateOf<String?>(null) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> if (uri != null && exportJson != null) context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(exportJson) } }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? -> if (uri != null) runCatching { context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { appDataFromJson(it.readText()) }?.let(vm::replaceAll) } }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Персонализация", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        item { SettingCard("Базовая валюта", "Используется для сводных расчётов") { OutlinedTextField(base, { base = it.uppercase(); vm.updateSettings(data.settings.copy(baseCurrency = base)) }, singleLine = true, modifier = Modifier.fillMaxWidth()) } }
        item { SettingCard("Тема", "Светлая, тёмная или системная") { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip("Система", theme == "system") { theme = "system"; vm.updateSettings(data.settings.copy(theme = theme)) }; FilterChip("Светлая", theme == "light") { theme = "light"; vm.updateSettings(data.settings.copy(theme = theme)) }; FilterChip("Тёмная", theme == "dark") { theme = "dark"; vm.updateSettings(data.settings.copy(theme = theme)) } } } }
        item { SettingCard("Акцент", "Статичный цвет или градиент") { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip("Градиент", mode == "gradient") { mode = "gradient"; vm.updateSettings(data.settings.copy(colorMode = mode)) }; FilterChip("Статичный", mode == "static") { mode = "static"; vm.updateSettings(data.settings.copy(colorMode = mode)) } } } }
        item { ColorPresetCard("Цвет 1", color1) { color1 = it; vm.updateSettings(data.settings.copy(color1 = it)) } }
        item { ColorPresetCard("Цвет 2", color2) { color2 = it; vm.updateSettings(data.settings.copy(color2 = it)) } }
        item {
            SettingCard("Резервная копия", "Ваши данные можно сохранить локально в JSON и восстановить без облака") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { exportJson = data.toJson(); exportLauncher.launch("astro-backup.json") }) { Text("Экспорт") }
                    OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/plain")) }) { Text("Импорт") }
                }
            }
        }
        item { SettingCard("О приложении", "Astro · локальный финансовый и личный планировщик") { Text("Все записи хранятся на устройстве. Внешняя синхронизация в этой версии не включена.", fontSize = 12.sp) } }
        item { Spacer(Modifier.height(30.dp)) }
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(22.dp)) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); content() } }
}

@Composable
private fun ColorPresetCard(title: String, value: Long, onPick: (Long) -> Unit) {
    val presets = listOf(0xFF7C3AED, 0xFF06B6D4, 0xFF0EA5E9, 0xFF10B981, 0xFFF59E0B, 0xFFEF4444, 0xFFEC4899, 0xFF111827)
    SettingCard(title, "Выберите цвет интерфейса") {
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) { presets.forEach { p -> Box(Modifier.size(38.dp).clip(CircleShape).background(Color(p)).clickable { onPick(p) }, contentAlignment = Alignment.Center) { if (p == value) Icon(Icons.Default.Check, null, tint = Color.White) } } }
    }
}
