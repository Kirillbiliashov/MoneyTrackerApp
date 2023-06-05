package com.example.moneytrackerapp.ui.homescreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.data.entity.ExpenseTuple
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
import com.example.moneytrackerapp.utils.DateUtils.toMillis
import com.example.moneytrackerapp.utils.formatSum
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.lang.Integer.min
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun HomeScreenContent(
    viewModel: HomeScreenViewModel,
    onHitLimit: (Limit) -> Unit,
    modifier: Modifier = Modifier
) {
    val factory = ViewModelProvider.Factory
    val categoriesViewModel: CategoriesScreenViewModel = viewModel(factory = factory)
    val categoriesUiState = categoriesViewModel.uiState.collectAsState()
    val settingsViewModel: SettingsScreenViewModel = viewModel(factory = factory)
    val uiState = viewModel.uiState.collectAsState()
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
        chosenCategories = categoriesUiState.value.chosenCategories,
        limits = settingsViewModel.limits.collectAsState(),
        onHitLimit = onHitLimit,
        viewModel = viewModel
    )
}

@Composable
fun HomeScreenData(
    chosenCategories: List<Category>,
    limits: State<List<Limit>>,
    onHitLimit: (Limit) -> Unit,
    viewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val currencyRate = uiState.value.currentCurrencyRate
    val expenseStatsDisplayed = uiState.value.expenseStatsDisplayed
    val expenses =
        uiState.value.displayExpenses.filterByCategories(chosenCategories)
    val expenseSum = expenses.fold(0.00) { acc, value -> acc + value.sum }
    val limit = limits.value.findLimit(uiState.value.localDateTimeRange)
    if (limit != null && expenseSum >= limit.sum) onHitLimit(limit)
    HomeScreenHeader(
        displayStats = expenseStatsDisplayed,
        viewModel = viewModel
    )
    Spacer(modifier = Modifier.height(40.dp))
    Text(
        text = currencyRate.formatSum(expenseSum),
        style = MaterialTheme.typography.displayLarge
    )
    Spacer(modifier = Modifier.height(40.dp))
    val expensesModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.83f)
        .expensesGraphics()
    if (expenseStatsDisplayed) {
        ExpensesStats(expenses = expenses, limit = limit,
            currencyRate = currencyRate,
            modifier = expensesModifier)
    } else {
        ExpensesList(expenses = expenses,
            currencyRate = currencyRate,
            modifier = expensesModifier)
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
fun ExpensesStats(expenses: List<ExpenseTuple>,
                  currencyRate: CurrencyRate,
                  limit: Limit?,
                  modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        val categoryExpensesMap = expenses.groupBy { it.categoryName }
        val chartValues = categoryExpensesMap.values.map { it.sumOf { e -> e.sum } }
        if (chartValues.isNotEmpty()) {
            item {
                ExpenseCharts(
                    chartValues = chartValues,
                    categoryExpensesMap = categoryExpensesMap,
                    currencyRate = currencyRate
                )
            }
        }
        if (limit != null) {
            item {
                LimitInfo(
                    limit = limit,
                    currencyRate = currencyRate,
                    expensesSum = chartValues.sum()
                )
            }
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
    val chartColors = colorsList(categoryExpensesMap.size)
    Text(
        text = "Allocation of expenses by categories:",
        style = MaterialTheme.typography.displayLarge, fontSize = 22.sp
    )
    Spacer(modifier = modifier.height(16.dp))
    Text(
        text = "Bar chart:",
        style = MaterialTheme.typography.displayLarge, fontSize = 20.sp
    )
    BarChart(
        data = barChartMap,
        currencyRate = currencyRate,
        maxValue = chartValues.max()
    )
    Text(
        text = "Pie chart:",
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
fun LimitInfo(
    limit: Limit,
    currencyRate: CurrencyRate,
    expensesSum: Double,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Limit (${limit.localDateRangeString()}): ",
        style = MaterialTheme.typography.displayLarge, fontSize = 20.sp,
        modifier = modifier.padding(bottom = 16.dp)
    )
    LinearProgressIndicator(
        progress = (expensesSum / limit.sum).toFloat(),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .height(15.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    )
    Text(
        text = "${currencyRate.formatSum(expensesSum)}/" +
                currencyRate.formatSum(limit.sum),
        modifier = modifier.padding(bottom = 32.dp, top = 8.dp),
        fontSize = 16.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenHeader(
    displayStats: Boolean,
    viewModel: HomeScreenViewModel, modifier: Modifier = Modifier
) {
    val sheetState = rememberSheetState()
    CalendarDialog(
        state = sheetState,
        selection = CalendarSelection.Date { viewModel.updateChosenDate(it) })
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { sheetState.show() }) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
        }
        Spacer(modifier = modifier.weight(1f))
        ExpenseModeSwitch(
            onSwitchClick = viewModel::toggleExpenseDisplayStyle,
            displayStats = displayStats
        )
        Spacer(modifier = modifier.weight(1f))
        CalendarDropdown(viewModel = viewModel)
    }
    DateItems(viewModel = viewModel)
}

@Composable
fun ExpenseModeSwitch(
    onSwitchClick: () -> Unit,
    displayStats: Boolean,
    modifier: Modifier = Modifier
) {
    Row {
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
            Text(text = "List", color = if (displayStats) primary else onPrimary)
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
            Text(text = "Stats", color = if (displayStats) onPrimary else primary)
        }
    }
}

@Composable
fun CalendarDropdown(viewModel: HomeScreenViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    Box(modifier = Modifier.padding(end = 8.dp)) {
        Text(
            text = uiState.value.calendarOption.toString().lowercase(1),
            modifier = modifier.clickable(onClick = { viewModel.expandDropdown() }),
            style = MaterialTheme.typography.displayMedium
        )
        DropdownMenu(
            expanded = uiState.value.dropdownExpanded,
            onDismissRequest = { viewModel.dismissDropdown() },
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            DropdownMenuOptions(onItemClick = { viewModel.changeDropdownOption(it) })
        }
    }
}

@Composable
fun DateItems(viewModel: HomeScreenViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.value.chosenDateIdx - 3
    )
    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .dateItemsGraphics(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(items = uiState.value.calendarOption.datesList) { item ->
            val color = if (item == uiState.value.chosenDate)
                MaterialTheme.colorScheme.inversePrimary
            else Color.Transparent
            Text(
                text = item, fontSize = 16.sp,
                modifier = modifier
                    .clickable(onClick = { viewModel.updateChosenDate(item) })
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
    val options = CalendarOption.values().map { it.toString().lowercase(1) }.toList()
    options.forEachIndexed { index, s ->
        DropdownMenuItem(text = { Text(text = s) },
            onClick = { onItemClick(index) })
    }
}

@Composable
fun ExpensesList(
    expenses: List<ExpenseTuple>,
    currencyRate: CurrencyRate,
    modifier: Modifier = Modifier
) {
    val categoryExpensesMap = expenses.groupBy { it.categoryName }
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
                Text(text = category, style = MaterialTheme.typography.displayMedium)
                categoryExpensesMap[category]?.forEach {
                    ExpenseCard(expense = it, currencyRate = currencyRate)
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
fun ExpenseCard(expense: ExpenseTuple,
                currencyRate: CurrencyRate,
                modifier: Modifier = Modifier) {
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
fun ExpenseCardContent(expense: ExpenseTuple,
                       currencyRate: CurrencyRate,
                       modifier: Modifier = Modifier) {
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

private fun String.lowercase(startIdx: Int) =
    "${this.substring(0, startIdx)}${this.substring(startIdx).lowercase()}"


private fun List<ExpenseTuple>.filterByCategories(categories: List<Category>) =
    filter { s -> categories.any { it.name == s.categoryName } }

private fun List<Limit>.findLimit(
    dateTimeRange: Pair<LocalDateTime, LocalDateTime>
): Limit? {
    val rangeStart = dateTimeRange.first.toMillis()
    val rangeEnd = dateTimeRange.second.toMillis()
    return find { rangeStart >= it.startDate && rangeEnd <= it.endDte }
}

private fun colorsList(listSize: Int) = List(size = listSize) { randomColor() }

private fun randomColor(): Color {
    val red = Random.nextInt(256)
    val green = Random.nextInt(256)
    val blue = Random.nextInt(256)
    return Color(red, green, blue)
}