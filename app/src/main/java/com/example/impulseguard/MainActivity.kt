package com.example.impulseguard

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
simport androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.impulseguard.ui.theme.ImpulseGuardTheme
import androidx.compose.foundation.clickable
// Simple data class to hold what we need about each app
data class AppInfo(
    val name: String,
    val packageName: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImpulseGuardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WatchedAppsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Queries all launchable apps on the device
fun getInstalledApps(context: android.content.Context): List<AppInfo> {
    val pm = context.packageManager
    val apps = pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
    return apps
        .filter { pm.getLaunchIntentForPackage(it.packageName) != null } // only apps with a launcher icon
        .map { AppInfo(name = pm.getApplicationLabel(it).toString(), packageName = it.packageName) }
        .sortedBy { it.name.lowercase() }
}

@Composable
fun WatchedAppsScreen(modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var allApps by remember { mutableStateOf(listOf<AppInfo>()) }
    var watchedApps by remember { mutableStateOf(listOf<AppInfo>()) }
    var showPicker by remember { mutableStateOf(false) }

    // Load installed apps once, the first time this screen appears
    LaunchedEffect(Unit) {
        allApps = getInstalledApps(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Watched Apps",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showPicker = true }) {
            Text("+ Add app to watch")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (watchedApps.isEmpty()) {
            Text(
                text = "No apps watched yet. Add one above.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn {
                items(watchedApps) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = app.name, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = {
                            watchedApps = watchedApps.filter { it.packageName != app.packageName }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        }
    }

    if (showPicker) {
        AppPickerDialog(
            allApps = allApps.filter { app -> watchedApps.none { it.packageName == app.packageName } },
            onDismiss = { showPicker = false },
            onAppSelected = { app ->
                watchedApps = watchedApps + app
                showPicker = false
            }
        )
    }
}

@Composable
fun AppPickerDialog(
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppSelected: (AppInfo) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select an app") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(allApps) { app ->
                    Text(
                        text = app.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable { onAppSelected(app) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}