package com.example.moneytrackerapp.ui.categoriesscreen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.ui.ViewModelProvider

@Composable
fun CategoriesSheetContent(viewModel: CategoriesScreenViewModel,
                           onButtonClick: () -> Unit, modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val chosenCategories = uiState.value.chosenCategories
    val allCategoriesChosen = viewModel.allCategoriesChosen
    if (uiState.value.dialogShown) {
        Dialog(
            onDismissRequest = viewModel::dismissDialog,
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Add Category", style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.W500
                        )
                        Divider(thickness = 2.dp)
                        OutlinedTextField(
                            value = uiState.value.addCategoryName,
                            onValueChange = viewModel::changeTextFieldValue,
                            label = { Text(text = "Category", fontSize = 16.sp) },
                            modifier = modifier.padding(top = 16.dp)
                        )
                        Row(
                            modifier = modifier
                                .padding(top = 32.dp)
                                .fillMaxWidth(0.7f)
                        ) {
                            Button(onClick = viewModel::dismissDialog) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = modifier.weight(1f))
                            Button(onClick = {
                                viewModel.saveCategory()
                                viewModel.dismissDialog()
                            }) {
                                Text(text = "Save")
                            }
                        }
                    }
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AllCategoriesOption(allCategoriesChosen = allCategoriesChosen)
        LazyColumn {
            items(items = viewModel.categories) {
                Row(
                    modifier = modifier
                        .clickable(onClick = { viewModel.changeChosenCategory(it) })
                        .padding(8.dp)
                ) {
                    Text(text = it.name, fontSize = 18.sp)
                    Spacer(modifier = modifier.weight(1f))
                    if (!allCategoriesChosen && chosenCategories.contains(it)) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    }
                }
            }
        }
        Spacer(modifier = modifier.weight(1f))
        FloatingActionButton(onClick = viewModel::showDialog, modifier = modifier
            .padding(vertical = 20.dp)
            .width(100.dp)) {
            Text(text = "+ Add", fontSize = 18.sp)
        }
        Button(onClick = onButtonClick, modifier = modifier.padding(bottom = 64.dp)) {
            Text(text = "Ok")
        }
    }
}

@Composable
fun AllCategoriesOption(
    allCategoriesChosen: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = {})
            .padding(8.dp)
    ) {
        Text(text = "All", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = modifier.weight(1f))
        if (allCategoriesChosen) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
        }
    }
}
