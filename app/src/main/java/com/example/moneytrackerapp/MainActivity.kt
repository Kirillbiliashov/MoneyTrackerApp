package com.example.moneytrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneytrackerapp.data.entity.Limit
import com.example.moneytrackerapp.data.entity.localDateRangeString
import com.example.moneytrackerapp.ui.ViewModelProvider
import com.example.moneytrackerapp.ui.homescreen.HomeScreenContent
import com.example.moneytrackerapp.ui.homescreen.HomeScreenViewModel
import com.example.moneytrackerapp.ui.theme.MoneyTrackerAppTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

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
    val viewModel: HomeScreenViewModel = viewModel(factory = ViewModelProvider.Factory)
    val snackbarHostState = remember { SnackbarHostState() }
    val channel = remember { Channel<Limit>(Channel.CONFLATED) }
    LaunchedEffect(channel) {
        channel.receiveAsFlow()
            .collect { limit -> displayLimitSnackbar(limit, snackbarHostState) }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = {
                AppBar(onSettingsClick = viewModel::displaySettingsSheet)
            })
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HomeScreenContent(
                viewModel = viewModel,
                onHitLimit = { l -> channel.trySend(l) },
                modifier = modifier.weight(1f)
            )
        }
    }
}

private suspend fun displayLimitSnackbar(
    limit: Limit,
    snackbarHostState: SnackbarHostState
) {
    snackbarHostState.showSnackbar(
        message = "You have hit the limit of ${limit.sum} on ${limit.localDateRangeString()}",
        actionLabel = "OK",
        duration = SnackbarDuration.Short
    )
}

@Composable
fun AppBar(onSettingsClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Money Tracker",
            style = MaterialTheme.typography.displayLarge,
            fontSize = 32.sp
        )
        Spacer(modifier = modifier.weight(1f))
        IconButton(onClick = onSettingsClick) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    MoneyTrackerAppTheme(darkTheme = false) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MoneyTrackerApplication()
        }
    }
}
