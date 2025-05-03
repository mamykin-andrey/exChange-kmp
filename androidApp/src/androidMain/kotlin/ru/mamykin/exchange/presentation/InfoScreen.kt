package ru.mamykin.exchange.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.mamykin.exchange.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppInfoScreen(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = AppStrings.ABOUT_TITLE)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                },
            )
        }, content = { innerPadding ->
            Box(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                Column {
                    val context = LocalContext.current
                    Text(
                        text = AppStrings.ABOUT_TEXT,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    Text(
                        text = AppStrings.ABOUT_OPEN_GITHUB_TITLE,
                        fontSize = 16.sp,
                        color = Color(0xFF0000FF),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickable { openBrowser(context, AppStrings.ABOUT_GITHUB_URL) }
                    )
                }
            }
        })
}

private fun openBrowser(context: Context, url: String) {
    val intent =
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
    context.startActivity(intent)
}

@Preview
@Composable
fun AppInfoScreenPreview() {
    AppInfoScreen(rememberNavController())
}