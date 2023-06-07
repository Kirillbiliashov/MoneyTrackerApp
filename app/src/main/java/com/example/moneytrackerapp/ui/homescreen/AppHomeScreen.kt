package com.example.moneytrackerapp.ui.homescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.R
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.data.entity.Income
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.entity.localDateRangeString
import com.example.moneytrackerapp.ui.ViewModelProvider
import com.example.moneytrackerapp.ui.categoriesscreen.CategoriesScreenViewModel
import com.example.moneytrackerapp.ui.categoriesscreen.CategoriesSheetContent
import com.example.moneytrackerapp.ui.expensescreen.ExpenseSheetContent
import com.example.moneytrackerapp.ui.settingsscreen.SettingsScreenViewModel
import com.example.moneytrackerapp.ui.settingsscreen.SettingsSheetContent
import com.example.moneytrackerapp.utils.CalendarOption
import com.example.moneytrackerapp.utils.CurrencyRate
import com.example.moneytrackerapp.utils.DateUtils
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import com.example.moneytrackerapp.utils.formatSum
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.util.Calendar
import kotlin.random.Random

@Composable
fun HomeScreenContent(
    viewModel: HomeScreenViewModel,
    uiState: State<HomeScreenUIState>,
    onHitLimit: (Limit) -> Unit,
    modifier: Modifier = Modifier
) {
    val factory = ViewModelProvider.Factory
    val categoriesViewModel: CategoriesScreenViewModel = viewModel(factory = factory)
    val categoriesUiState = categoriesViewModel.uiState.collectAsState()
    val settingsViewModel: SettingsScreenViewModel = viewModel(factory = factory)
    val currencyRate = uiState.value.currentCurrencyRate
    SheetContent(
        visible = uiState.value.expenseSheetDisplayed,
        onHideSheet = viewModel::hideExpenseSheet
    ) {
        ExpenseSheetContent(
            onSaveClick = viewModel::hideExpenseSheet,
            currencyRate = currencyRate
        )
    }
    SheetContent(
        visible = uiState.value.categoriesSheetDisplayed,
        onHideSheet = viewModel::hideCategoriesSheet
    ) {
        CategoriesSheetContent(
            viewModel = categoriesViewModel,
            uiState = categoriesUiState,
            onButtonClick = viewModel::hideCategoriesSheet
        )
    }
    SheetContent(
        visible = uiState.value.settingsSheetDisplayed,
        onHideSheet = viewModel::hideSettingsSheet
    ) {
        SettingsSheetContent(
            viewModel = settingsViewModel,
            onSaveFileClick = viewModel::saveExpensesToFile,
            onButtonClick = viewModel::hideSettingsSheet,
            currencyRate = currencyRate,
            onUpdateCurrency = viewModel::updateChosenCurrency

        )
    }
    HomeScreenData(
        uiState = uiState,
        chosenCategories = categoriesUiState.value.chosenCategories,
        limits = settingsViewModel.limits.collectAsState(),
        incomeHistory = settingsViewModel.incomeHistory.collectAsState(),
        onHitLimit = onHitLimit,
        viewModel = viewModel
    )
}

@Composable
fun HomeScreenData(
    uiState: State<HomeScreenUIState>,
    chosenCategories: List<Category>,
    limits: State<List<Limit>>,
    incomeHistory: State<List<Income>>,
    onHitLimit: (Limit) -> Unit,
    viewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val currencyRate = uiState.value.currentCurrencyRate
    val expenseStatsDisplayed = uiState.value.expenseStatsDisplayed
    val expenses =
        uiState.value.displayExpenses.filterByCategories(chosenCategories)
    val categoryExpensesMap = expenses.groupBy { it.categoryName }
    val expenseSum = expenses.fold(0.00) { acc, value -> acc + value.sum }
    val dateRange = uiState.value.localDateTimeRange
    val dateLimits = limits.value.findLimits(dateRange)
    dateLimits.checkLimits(categoryExpensesMap.values.flatten(), onHitLimit)
    HomeScreenHeader(
        displayStats = expenseStatsDisplayed,
        uiState = uiState,
        viewModel = viewModel
    )
    Spacer(modifier = modifier.height(40.dp))
    Text(
        text = currencyRate.formatSum(expenseSum),
        style = MaterialTheme.typography.displayLarge
    )
    Spacer(modifier = modifier.height(40.dp))
    val expensesModifier = modifier
        .fillMaxWidth()
        .fillMaxHeight(0.83f)
        .expensesGraphics()
    if (expenseStatsDisplayed) {
        ExpensesStats(
            categoryExpensesMap = categoryExpensesMap,
            limits = dateLimits,
            incomeHistory = incomeHistory.value.incomeForMonth(dateRange),
            currencyRate = currencyRate,
            modifier = expensesModifier
        )
    } else {
        ExpensesList(
            categoryExpensesMap = categoryExpensesMap,
            currencyRate = currencyRate,
            modifier = expensesModifier
        )
    }
    HomeScreenButtons(
        onShowCategoriesSheet = viewModel::displayCategoriesSheet,
        onShowEditSheet = viewModel::displayExpenseSheet
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetContent(
    visible: Boolean, onHideSheet: () -> Unit,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    AnimatedVisibility(visible = visible) {
        ModalBottomSheet(onDismissRequest = onHideSheet, sheetState = sheetState) {
            content()
        }
    }
}

@Composable
fun HomeScreenButtons(
    onShowCategoriesSheet: () -> Unit,
    onShowEditSheet: () -> Unit, modifier: Modifier = Modifier
) {
    Row(modifier = Modifier.padding(bottom = 32.dp, start = 16.dp, end = 16.dp)) {
        HomeScreenFAB(
            imageVector = Icons.Rounded.List,
            onClick = onShowCategoriesSheet
        )
        Spacer(modifier = modifier.weight(1F))
        HomeScreenFAB(
            imageVector = Icons.Rounded.Edit,
            onClick = onShowEditSheet
        )
    }
}

@Composable
fun ExpensesStats(
    categoryExpensesMap: Map<String, List<ExpenseTuple>>,
    currencyRate: CurrencyRate,
    limits: List<Limit>,
    incomeHistory: List<Income>,
    modifier: Modifier = Modifier
) {
        LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
            val chartValues = categoryExpensesMap.values
                .map { it.sumOf { e -> e.sum } }
            if (chartValues.isNotEmpty()) {
                item {
                    ExpenseCharts(
                        chartValues = chartValues,
                        categoryExpensesMap = categoryExpensesMap,
                        currencyRate = currencyRate
                    )
                }
            }
            items(items = incomeHistory) {
                ProgressInfo(
                    header = stringResource(
                        R.string.expenses_to_income,
                        it.yearMonthStr
                    ),
                    currencyRate = currencyRate,
                    expensesSum = categoryExpensesMap.values.flatten()
                        .expensesForYearMonth(it.month, it.year),
                    maxValue = it.sum
                )
            }
            items(items = limits) { limit ->
                val limitPeriodExpenses = categoryExpensesMap.values.flatten()
                    .expenseSumForLimit(limit)
                ProgressInfo(
                    header = stringResource(
                        R.string.limit,
                        limit.localDateRangeString()
                    ),
                    currencyRate = currencyRate,
                    expensesSum = limitPeriodExpenses,
                    maxValue = limit.sum
                )
            }
        }
}

@Composable
fun ExpenseCharts(
    chartValues: List<Double>,
    categoryExpensesMap: Map<String, List<ExpenseTuple>>,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    val barChartMap = chartValues.zip(categoryExpensesMap.keys.toList()).toMap()
    val chartColors = remember { colorsList(categoryExpensesMap.size) }
    Text(
        text = stringResource(R.string.allocation_of_expenses),
        style = MaterialTheme.typography.displayLarge, fontSize = 22.sp
    )
    Spacer(modifier = modifier.height(16.dp))
    Text(
        text = stringResource(R.string.bar_chart),
        style = MaterialTheme.typography.displayLarge, fontSize = 20.sp
    )
    BarChart(
        data = barChartMap,
        currencyRate = currencyRate,
        maxValue = chartValues.max()
    )
    Text(
        text = stringResource(R.string.pie_chart),
        style = MaterialTheme.typography.displayLarge, fontSize = 20.sp
    )
    PieChartHeader(
        categoryExpensesMap = categoryExpensesMap,
        chartColors = chartColors
    )
    PieChart(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        colors = chartColors,
        inputValues = chartValues
    )
}

@Composable
fun ProgressInfo(
    header: String,
    maxValue: Double,
    currencyRate: CurrencyRate,
    expensesSum: Double,
    modifier: Modifier = Modifier
) {
    Text(
        text = header,
        style = MaterialTheme.typography.displayLarge, fontSize = 20.sp,
        modifier = modifier.padding(bottom = 16.dp)
    )
    LinearProgressIndicator(
        progress = (expensesSum / maxValue).toFloat(),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .height(15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    )
    Text(
        text = "${currencyRate.formatSum(expensesSum)}/" +
                currencyRate.formatSum(maxValue),
        modifier = modifier.padding(bottom = 32.dp, top = 8.dp),
        fontSize = 16.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenHeader(
    displayStats: Boolean,
    uiState: State<HomeScreenUIState>,
    viewModel: HomeScreenViewModel, modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.value.chosenDateIdx
    )
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberSheetState()
    CalendarDialog(
        state = sheetState,
        selection = CalendarSelection.Date {
            viewModel.updateChosenDate(it)
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.value.chosenDateIdx)
            }
        })
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { sheetState.show() }) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
        }
        Spacer(modifier = modifier.weight(1f))
        ExpenseModeSwitch(
            onSwitchClick = viewModel::toggleExpenseDisplayStyle,
            displayStats = displayStats
        )
        Spacer(modifier = modifier.weight(0.72f))
        CalendarDropdown(
            onChangeOption = {
                viewModel.changeDropdownOption(it)
                coroutineScope.launch {
                    listState.animateScrollToItem(uiState.value.chosenDateIdx)
                }
            },
            calendarOption = uiState.value.calendarOption
        )
    }
    DateItems(
        onDateClick = {
            viewModel.updateChosenDate(it)
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.value.chosenDateIdx)
            }
        },
        chosenDate = uiState.value.chosenDate,
        calendarOption = uiState.value.calendarOption,
        listState = listState
    )
}

@Composable
fun ExpenseModeSwitch(
    onSwitchClick: () -> Unit,
    displayStats: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.height(35.dp)) {
        val primary = MaterialTheme.colorScheme.primary
        val onPrimary = MaterialTheme.colorScheme.onPrimary
        Button(
            onClick = onSwitchClick,
            shape = RoundedCornerShape(
                topStart = 50.dp,
                topEnd = 0.dp,
                bottomStart = 50.dp,
                bottomEnd = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (displayStats) onPrimary else primary
            ),
            border = BorderStroke(1.dp, primary)
        ) {
            Text(
                text = stringResource(id = R.string.list),
                color = if (displayStats) primary else onPrimary
            )
        }
        Button(
            onClick = onSwitchClick,
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 50.dp,
                bottomStart = 0.dp,
                bottomEnd = 50.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (displayStats) primary else onPrimary
            ),
            border = BorderStroke(1.dp, primary)
        ) {
            Text(
                text = stringResource(R.string.stats),
                color = if (displayStats) onPrimary else primary
            )
        }
    }
}

@Composable
fun CalendarDropdown(
    onChangeOption: (Int) -> Unit,
    calendarOption: CalendarOption,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(72.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = calendarOption.toString().lowercase(1),
            modifier = modifier.clickable(onClick = { expanded = true }),
            style = MaterialTheme.typography.displayMedium,
            fontSize = 18.sp,
            textDecoration = TextDecoration.Underline
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            DropdownMenuOptions(onItemClick = {
                expanded = false
                onChangeOption(it)
            })
        }
    }
}

@Composable
fun DateItems(
    onDateClick: (String) -> Unit,
    calendarOption: CalendarOption,
    chosenDate: String,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .dateItemsGraphics(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = calendarOption.datesList) { item ->
            val color = if (item == chosenDate)
                MaterialTheme.colorScheme.inversePrimary
            else Color.Transparent
            Text(
                text = item, fontSize = when (calendarOption) {
                    CalendarOption.DAILY -> 16.sp
                    CalendarOption.MONTHLY -> 18.sp
                    CalendarOption.WEEKLY -> 12.5.sp
                },
                modifier = modifier
                    .clickable(onClick = { onDateClick(item) })
                    .background(color, shape = RoundedCornerShape(100.dp))
                    .padding(8.dp)
            )
        }
    }
}

private fun Modifier.dateItemsGraphics() =
    graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.horizontalGradient(
                    0f to Color.Transparent,
                    0.2f to Color.Red, 0.8f to Color.Red, 1f to Color.Transparent
                ),
                blendMode = BlendMode.DstIn
            )
        }

@Composable
fun DropdownMenuOptions(
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = CalendarOption.values().dropdownList()
    options.forEachIndexed { index, s ->
        DropdownMenuItem(text = { Text(text = s) },
            onClick = { onItemClick(index) })
    }
}

@Composable
fun ExpensesList(
    categoryExpensesMap: Map<String, List<ExpenseTuple>>,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    if (categoryExpensesMap.isEmpty()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_expenses_msg),
                style = MaterialTheme.typography.displayMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items = categoryExpensesMap.keys.toList()) { category ->
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.displayMedium
                    )
                    categoryExpensesMap[category]?.forEach {
                        ExpenseCard(expense = it, currencyRate = currencyRate)
                    }
                }
            }
        }
    }
}

private fun Modifier.expensesGraphics() =
    graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    0.9f to Color.Red,
                    1f to Color.Transparent
                ),
                blendMode = BlendMode.DstIn
            )
        }

@Composable
fun HomeScreenFAB(
    imageVector: ImageVector, onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = Color.White,
        )
    }
}

@Composable
fun ExpenseCard(
    expense: ExpenseTuple,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .defaultMinSize(minHeight = 64.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Spacer(modifier = modifier.weight(1F))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            ExpenseCardContent(expense = expense, currencyRate = currencyRate)
        }
        Spacer(modifier = modifier.weight(1F))
    }
}

@Composable
fun ExpenseCardContent(
    expense: ExpenseTuple,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = expense.name, fontSize = 18.sp)
        Spacer(modifier = modifier.weight(1F))
        Text(text = currencyRate.formatSum(expense.sum), fontSize = 18.sp)
    }
    if (expense.note != null) {
        Text(
            text = expense.note,
            fontWeight = FontWeight.W100,
            modifier = modifier.padding(top = 4.dp)
        )
    }
}

private fun List<ExpenseTuple>.filterByCategories(categories: List<Category>) =
    filter { s -> categories.any { it.name == s.categoryName } }

private fun List<ExpenseTuple>.expenseSumForLimit(limit: Limit) =
    filter { it.date in (limit.startDate..limit.endDte) }
        .sumOf { it.sum }

private fun List<ExpenseTuple>.expensesForYearMonth(month: Int, year: Int): Double {
    val range = DateUtils.monthRangeMillis(month, year)
    return filter { it.date in range.first..range.second }.sumOf { it.sum }
}

private fun List<Limit>.findLimits(
    dateTimeRange: Pair<LocalDateTime, LocalDateTime>
): List<Limit> {
    val rangeStart = dateTimeRange.first.toMillis()
    val rangeEnd = dateTimeRange.second.toMillis()
    return filter { it.startDate >= rangeStart && it.endDte <= rangeEnd }
}

private fun List<Income>.incomeForMonth(
    range: Pair<LocalDateTime, LocalDateTime>
): List<Income> {
    val rangeStartMonth = range.first.monthValue
    val rangeEndMonth = range.second.monthValue
    return filter { it.month in rangeStartMonth..rangeEndMonth }
}

private fun List<Limit>.checkLimits(expenses: List<ExpenseTuple>,
                                    onHitLimit: (Limit) -> Unit) {
    forEach {
        val periodExpenses = expenses.expenseSumForLimit(it)
        if (periodExpenses >= it.sum) {
            onHitLimit(it)
        }
    }
}

private fun Array<CalendarOption>.dropdownList() =
    map { it.toString().lowercase(1) }.toList()

private fun String.lowercase(startIdx: Int) =
    "${this.substring(0, startIdx)}${this.substring(startIdx).lowercase()}"

private fun colorsList(listSize: Int) = List(size = listSize) { randomColor() }

private fun randomColor(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}