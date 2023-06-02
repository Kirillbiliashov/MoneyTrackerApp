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
import androidx.compose.runtime.State
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
import com.example.moneytrackerapp.data.entity.Category
import com.example.moneytrackerapp.ui.ViewModelProvider

@Composable
fun CategoriesSheetContent(
    viewModel: CategoriesScreenViewModel,
    uiState: State<CategoriesScreenUIState>,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chosenCategories = uiState.value.chosenCategories
    if (uiState.value.dialogShown) {
        AddCategoryDialog(
            textFieldValue = uiState.value.addCategoryName,
            viewModel = viewModel
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CategoriesList(
            chosenCategories = chosenCategories,
            viewModel = viewModel
        )
        Spacer(modifier = modifier.weight(1f))
        AddDialogButton(onClick = viewModel::showDialog)
        Button(onClick = onButtonClick, modifier = modifier.padding(bottom = 64.dp)) {
            Text(text = "Ok")
        }
    }
}

@Composable
fun AddDialogButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick, modifier = modifier
            .padding(vertical = 20.dp)
            .width(100.dp)
    ) {
        Text(text = "+ Add", fontSize = 18.sp)
    }
}

@Composable
fun CategoriesList(
    chosenCategories: List<Category>,
    viewModel: CategoriesScreenViewModel, modifier: Modifier = Modifier
) {
    AllCategoriesOption(allCategoriesChosen = viewModel.allCategoriesChosen)
    LazyColumn {
        items(items = viewModel.categories) {
            Row(
                modifier = modifier
                    .clickable(onClick = { viewModel.changeChosenCategory(it) })
                    .padding(8.dp)
            ) {
                Text(text = it.name, fontSize = 18.sp)
                Spacer(modifier = modifier.weight(1f))
                if (!viewModel.allCategoriesChosen && chosenCategories.contains(it)) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    textFieldValue: String,
    viewModel: CategoriesScreenViewModel,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = viewModel::dismissDialog,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
            ) {
                AddCategoryDialogContent(
                    textFieldValue = textFieldValue,
                    viewModel = viewModel
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

@Composable
fun AddCategoryDialogContent(
    textFieldValue: String,
    viewModel: CategoriesScreenViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddCategoryDialogHeader()
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = viewModel::changeTextFieldValue,
            label = { Text(text = "Category", fontSize = 16.sp) },
            modifier = modifier.padding(top = 16.dp)
        )
        AddCategoryDialogButtons(
            onCancelClick = viewModel::dismissDialog,
            onSaveClick = {
                viewModel.saveCategory()
                viewModel.dismissDialog()
            }
        )
    }
}

@Composable
fun AddCategoryDialogHeader(modifier: Modifier = Modifier) {
    Text(
        text = "Add Category", style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.W500
    )
    Divider(thickness = 2.dp)
}

@Composable
fun AddCategoryDialogButtons(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 32.dp)
            .fillMaxWidth(0.7f)
    ) {
        Button(onClick = onCancelClick) {
            Text(text = "Cancel")
        }
        Spacer(modifier = modifier.weight(1f))
        Button(onClick = onSaveClick) {
            Text(text = "Save")
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
