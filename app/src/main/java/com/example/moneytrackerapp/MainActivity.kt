package com.example.moneytrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.ui.expensescreen.ExpenseScreenViewModel
import com.example.moneytrackerapp.ui.homescreen.HomeScreenViewModel
import com.example.moneytrackerapp.ui.theme.MoneyTrackerAppTheme
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoneyTrackerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MoneyTrackerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyTrackerApp(modifier: Modifier = Modifier) {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "Money Tracker") }) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeScreenContent(
                modifier = modifier.weight(1f)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(modifier: Modifier = Modifier) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val viewModel: HomeScreenViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState()
    if (uiState.value.bottomSheetDisplayed) {
        ModalBottomSheet(
            onDismissRequest = viewModel::hideBottomSheet,
            sheetState = sheetState
        ) {
            BottomSheetContent(onSaveClick = viewModel::hideBottomSheet)
        }
    }
    DatesHeader(viewModel = viewModel)
    Spacer(modifier = Modifier.height(40.dp))
    Text(text = "$0.00", fontSize = 48.sp)
    Spacer(modifier = Modifier.height(40.dp))
    ExpensesList(modifier = modifier)
    HomeScreenButtons(onShowEditSheet = viewModel::displayBottomSheet)
}


@Composable
fun BottomSheetContent(onSaveClick: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: ExpenseScreenViewModel = viewModel()
    val uiState = viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add expense", fontSize = 32.sp)
        OutlinedTextField(
            value = uiState.value.name,
            onValueChange = viewModel::updateExpenseName,
            label = { Text(text = "Expense name") },
            modifier = modifier.padding(vertical = 8.dp)
        )
        OutlinedTextField(
            value = uiState.value.sum.toString(),
            onValueChange = viewModel::updateExpenseSum,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Sum") },
            modifier = modifier.padding(vertical = 8.dp)
        )
        CategoryDropdown(
            expanded = uiState.value.dropdownExpanded,
            onDropdownIconClick = viewModel::toggleDropdown,
            onHideDropdown = viewModel::hideDropdownOptions
        )
        OutlinedTextField(
            value = uiState.value.note ?: "",
            onValueChange = viewModel::updateExpenseNote,
            label = { Text(text = "Note") },
            modifier = modifier.padding(vertical = 8.dp))
        Button(onClick = onSaveClick) {
            Text(text = "Save")
        }
    }
}

@Composable
fun CategoryDropdown(
    expanded: Boolean,
    onDropdownIconClick: () -> Unit,
    onHideDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        OutlinedTextField(
            value = "Select category",
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = modifier.clickable(onClick = onDropdownIconClick)
                )
            },
            onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(text = "Category") },
            modifier = modifier.padding(vertical = 8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onHideDropdown,
        ) {
            Datasource.categories.forEach {
                DropdownMenuItem(
                    text = { Text(text = it) },
                    onClick = onHideDropdown
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatesHeader(viewModel: HomeScreenViewModel, modifier: Modifier = Modifier) {
    val sheetState = rememberSheetState()
    CalendarDialog(
        state = sheetState,
        selection = CalendarSelection.Date { viewModel.updateChosenDate(it) })
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically,
    ) {
        IconButton(onClick = { sheetState.show() }) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
        }
        Spacer(modifier = modifier.weight(1f))
        CalendarDropdown(viewModel = viewModel)
    }
    DateItems(viewModel = viewModel)
}

@Composable
fun CalendarDropdown(viewModel: HomeScreenViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    Box(modifier = Modifier.padding(end = 8.dp)) {
        Text(
            text = viewModel.currentCalendarOption,
            modifier = modifier.clickable(onClick = { viewModel.expandDropdown() })
        )
        DropdownMenu(
            expanded = uiState.value.dropdownExpanded,
            onDismissRequest = { viewModel.dismissDropdown() },
            modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            DropdownMenuOptions(
                options = viewModel.calendarOptions,
                onItemClick = { viewModel.changeDropdownOption(it) })
        }
    }
}



@Composable
fun DateItems(viewModel: HomeScreenViewModel, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = CenterVertically
    ) {
        items(items = viewModel.dateItems) { item ->
            val color = if (item == uiState.value.chosenDate)
                Color(177, 188, 247, 255)
            else Color.Transparent
            Text(
                text = item, fontSize = 18.sp,
                modifier = modifier
                    .clickable(onClick = { viewModel.updateChosenDate(item) })
                    .background(color, shape = RoundedCornerShape(100.dp))
                    .padding(end = 8.dp, start = 8.dp)
            )
        }
    }
}

@Composable
fun DropdownMenuOptions(
    options: List<String>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    options.forEachIndexed { index, s ->
        DropdownMenuItem(text = { Text(text = s) },
            onClick = { onItemClick(index) })
    }
}


@Composable
fun ExpensesList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = Datasource.categories) { category ->
            CategorySection(
                category = category,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun HomeScreenButtons(onShowEditSheet: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = Modifier.padding(bottom = 32.dp, start = 16.dp, end = 16.dp)) {
        FloatingActionButton(
            onClick = { },
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = null,
                tint = Color.White,
            )
        }
        Spacer(modifier = modifier.weight(1F))
        FloatingActionButton(
            onClick = onShowEditSheet,
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}


@Composable
fun CategorySection(category: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = category, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Datasource.getExpenses(category).forEach {
            ExpenseCard(expense = it)
        }
    }
}

@Composable
fun ExpenseCard(expense: Pair<String, Double>, modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .height(64.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = CenterVertically
        ) {
            Text(text = expense.first, fontSize = 18.sp)
            Spacer(modifier = modifier.weight(1F))
            Text(text = expense.second.toString(), fontSize = 18.sp)
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    MoneyTrackerAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MoneyTrackerApp()
        }
    }
}
