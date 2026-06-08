package app.bytejoseph.tunnelvoice

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.ExposurePlus2
import androidx.compose.material.icons.outlined.ExposurePlus1
import androidx.compose.material.icons.outlined.ExposurePlus2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.bytejoseph.tunnelvoice.data.AuthManager
import app.bytejoseph.tunnelvoice.models.TabItem
import app.bytejoseph.tunnelvoice.ui.components.MainScreen
import app.bytejoseph.tunnelvoice.ui.theme.TunnelVoiceTheme

class MainActivity : ComponentActivity() {
    private val voiceViewModel: VoiceViewModel by viewModels()
    private val authManager = AuthManager()

    private val tabItems = listOf(
        TabItem(
            title = "Account 1",
            unselectedIcon = Icons.Outlined.ExposurePlus1,
            selectedIcon = Icons.Filled.ExposurePlus1
        ), TabItem(
            title = "Account 2",
            unselectedIcon = Icons.Outlined.ExposurePlus2,
            selectedIcon = Icons.Filled.ExposurePlus2
        )
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TunnelVoiceTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MainScreen(
                            vm = voiceViewModel,
                            tabItems = tabItems,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        askFullFilePermission(this)
        authManager.signInAnonymously { success ->
            // Handle success or failure if needed
        }
    }
}
