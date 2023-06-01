package com.example.moneytrackerapp.ui.categoriesscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.ui.ViewModelProvider

@Composable
fun CategoriesSheetContent(viewModel: CategoriesScreenViewModel,
    onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    val uiState = viewModel.uiState.collectAsState()
    val chosenCategories = uiState.value.chosenCategories
    val allCategoriesChosen = viewModel.allCategoriesChosen
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
        Button(onClick = onButtonClick, modifier = modifier.padding(bottom = 48.dp)) {
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