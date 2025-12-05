package app.bytejoseph.tunnelvoice

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.bytejoseph.tunnelvoice.ui.theme.TunnelVoiceTheme
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class VoiceNotes(
    val name: String
)
class VoiceViewModel : ViewModel() {
    var isPlaying by mutableStateOf(false)
        private set

    fun toggleplay() {
        isPlaying = !isPlaying
    }
}

class MainActivity : ComponentActivity() {
    private val voiceViewModel: VoiceViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TunnelVoiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth().background(MaterialTheme.colorScheme.surface),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(
                            name = "Android"
                        )
                        val today = LocalDate.now()
                        val formatted = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        DateLabel(formatted)
                        VoiceMsg(voiceViewModel)
                        VoiceMsg(voiceViewModel)
                        VoiceMsg(voiceViewModel)
                        VoiceMsg(voiceViewModel)

                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!"
    )
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VoiceMsg(vm: VoiceViewModel) {
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = { isPlaying = !isPlaying })
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Box {
            if (isPlaying) {
                CircularWavyProgressIndicator(color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            } else {
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(5.dp))

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.0f)
        ) {
            if (isPlaying) {
                LinearWavyProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    amplitude = { 0.7f },
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LinearProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                "9:10 am",
                modifier = Modifier.align(Alignment.BottomEnd),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )

    }
}


@Composable
fun DateLabel(name: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.primary)
            .wrapContentSize()
    ) {
        Text(
            name,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .wrapContentHeight(),
            fontSize = 13.sp
        )
    }
}

@Preview
@Composable
fun PreviewDateLabel() {
    DateLabel(name = "Today")
}