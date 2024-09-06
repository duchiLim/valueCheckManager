package com.example.valuecheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.validation.ValueCheckManager
import com.example.valuecheck.model.LoginData
import com.example.valuecheck.model.MemberData
import com.example.valuecheck.ui.theme.ValueCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val mockData = getMockData()
        try {
            ValueCheckManager.Builder()
                .addParseData(mockData)
                .build()
                .validate()
        } catch (e: Exception) {
            // ignore
        }

        setContent {
            ValueCheckTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun getMockData(): LoginData {
        return LoginData().apply {
            loginToken = null
            member = MemberData().apply {
                memberNo = 123456
                name = "duchi"
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ValueCheckTheme {
        Greeting("Android")
    }
}