package com.example.moneytrackerapp.ui.settingsscreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.ui.ViewModelProvider
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.CalendarView
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheetContent(onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel: SettingsScreenViewModel = viewModel(factory = ViewModelProvider.Factory)
    val limits = viewModel.limits.collectAsState()
    var limitDialogDisplayed by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        if (limitDialogDisplayed) {
            val sheetState = rememberSheetState(
                onCloseRequest = {
                    if (viewModel.uiState.value.chosenDates.isNotEmpty()) {
                        viewModel.saveLimit()
                    }
                    limitDialogDisplayed = false
                })
            Dialog(
                onDismissRequest = { limitDialogDisplayed = false },
                content = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75f)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Add Limit",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.W500
                            )
                            Divider(thickness = 2.dp)
                            LimitSumTextField(
                                uiState = viewModel.uiState.collectAsState(),
                                onValueChange = viewModel::updateLimitSum
                            )
                            CalendarView(
                                sheetState = sheetState,
                                selection = CalendarSelection.Dates { viewModel.updateChosenDates(it) })
                        }
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Limits", style = MaterialTheme.typography.displayMedium)
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            Spacer(modifier = modifier.weight(1F))
            Button(onClick = { limitDialogDisplayed = true }) {
                Text(text = "Add limit")
            }
        }
        LazyColumn(modifier = modifier.border(2.dp, Color.Red)) {
            items(limits.value) {
                Row {
                    Text(text = "${localDateString(it.startDate)} - ${localDateString(it.endDte)}")
                    Spacer(modifier = modifier.weight(1F))
                    Text(text = it.sum.toString())
                }
            }
        }
    }

}


@Composable
fun LimitSumTextField(
    uiState: State<SettingsScreenUIState>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = uiState.value.currentLimitSum.toString(),
        onValueChange = onValueChange,
        label = { Text(text = "Sum", fontSize = 16.sp) },
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        modifier = modifier.padding(top = 16.dp)
    )
}

private fun localDateString(msecs: Long): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    dateFormat.timeZone = TimeZone.getDefault()
    val date = Date(msecs)
    return dateFormat.format(date)
}