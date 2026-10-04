package com.example.ui.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.viewmodel.SecureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScreen(viewModel: SecureViewModel) {
    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var currentUrl by remember { mutableStateOf("https://www.google.com") }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var showAutofillDialog by remember { mutableStateOf(false) }

    val bookmarks = listOf(
        "Google" to "https://www.google.com",
        "Official Docs" to "https://developer.android.com",
        "GitHub" to "https://github.com",
        "Firebase Console" to "https://console.firebase.google.com"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar URL & Controls
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Public, contentDescription = null) },
                        trailingIcon = {
                            if (urlInput.isNotEmpty()) {
                                IconButton(onClick = { urlInput = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        label = { Text("Portal URL") }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            var target = urlInput.trim()
                            if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                target = "https://$target"
                            }
                            currentUrl = target
                            webViewRef?.loadUrl(target)
                            viewModel.logTask("Portal Navigation", "Navigated to $target")
                        },
                        modifier = Modifier.height(54.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Go")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Bookmarks & Helper Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookmarks) { (name, url) ->
                            SuggestionChip(
                                onClick = {
                                    urlInput = url
                                    currentUrl = url
                                    webViewRef?.loadUrl(url)
                                    viewModel.logTask("Bookmark Open", "Opened $name")
                                },
                                label = { Text(name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledTonalButton(
                        onClick = { showAutofillDialog = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Autofill Helper")
                    }
                }
            }
        }

        // WebView Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadsImagesAutomatically = true
                        webViewClient = WebViewClient()
                        loadUrl(currentUrl)
                        webViewRef = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (showAutofillDialog) {
        AlertDialog(
            onDismissRequest = { showAutofillDialog = false },
            title = { Text("Secure Autofill Assistant") },
            text = {
                Column {
                    Text("Select saved official credentials to inject into the active portal form:")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            webViewRef?.evaluateJavascript(
                                "javascript:(function() { " +
                                        "var user = document.querySelector('input[type=email], input[name*=user], input[id*=user]');" +
                                        "if(user) user.value = 'official_admin@secureportal.gov';" +
                                        "})();", null
                            )
                            showAutofillDialog = false
                            viewModel.logTask("Autofill Executed", "Injected official credentials into portal form.")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Inject Official Admin Credentials")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAutofillDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
