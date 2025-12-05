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
import java.io.File
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import app.bytejoseph.tunnelvoice.askFullFilePermission
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.media.MediaPlayer
import java.util.Timer
import java.util.TimerTask
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun getLastModifiedName(path: String): String? {
    val dir = File(path)
    if (!dir.exists() || !dir.isDirectory) return null

    val children = dir.listFiles() ?: return null
    val lastModified = children.maxByOrNull { it.lastModified() } ?: return null

    return lastModified.name
}

data class VoiceNotes(
    val name: String
)

class VoiceViewModel : ViewModel() {
    // Audio list
    var audioList = mutableStateListOf<VoiceNotes>()

    // Playback state
    var isPlaying by mutableStateOf(false)
        private set

    // Compose state for playback progress (0.0f to 1.0f)
    var progressRatio by mutableStateOf(0f)
        private set

    private var player: MediaPlayer? = null
    private var progressTimer: Timer? = null

    val targetPath =
        "/storage/emulated/0/Android/media/com.whatsapp/whatsapp/Media/WhatsApp Voice Notes"
    val lastFolder = targetPath + "/" + getLastModifiedName(targetPath)

    init {
        loadAudioFiles()
    }

    /** Load WhatsApp voice notes from storage */
    private fun loadAudioFiles() {

        val dir = File(lastFolder)
        if (!dir.exists() || !dir.isDirectory) return

        val files = dir.listFiles()?.filter { it.isFile && it.name != ".nomedia" } ?: return

        audioList.clear()
        files.forEach { file ->
            audioList.add(VoiceNotes(file.name))
        }
    }

    /** Play an audio file */
    fun play(filePath: String) {
        val filePath = "$lastFolder/$filePath"
        stop() // stop previous playback

        val file = File(filePath)
        if (!file.exists()) return

        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnPreparedListener { mp ->
                mp.start()
                startProgressUpdates()
            }
            setOnCompletionListener {
                stop()
            }
            prepareAsync()
        }
        isPlaying = true
    }

    fun pause() {
        player?.pause()
        isPlaying = false
    }

    fun resume() {
        player?.start()
        isPlaying = true
    }

    fun seekTo(ms: Int) {
        player?.seekTo(ms)
    }

    fun stop() {
        progressTimer?.cancel()
        progressTimer = null
        player?.release()
        player = null
        isPlaying = false
        progressRatio = 0f
    }

    /** Updates progressRatio every 200ms */
    private fun startProgressUpdates() {
        viewModelScope.launch {
            while (player != null && player!!.isPlaying && isActive) {
                val mp = player!!
                val pos = mp.currentPosition
                val dur = mp.duration

                // Safe update on main thread
                progressRatio = if (dur > 0) pos.toFloat() / dur else 0f

                delay(200) // 200ms update interval
            }
        }
    }

    /** Helper to get last modified folder name */
    private fun getLastModifiedName(path: String): String {
        val dir = File(path)
        val lastModifiedFile = dir.listFiles()?.maxByOrNull { it.lastModified() }
        return lastModifiedFile?.name ?: ""
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
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(
                            name = "Android"
                        )
                        val today = LocalDate.now()
                        val formatted = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        DateLabel(formatted)
                        Messages(vm = voiceViewModel)


                    }

                }
            }
        }
        askFullFilePermission(this)

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
fun VoiceMsg(vm: VoiceViewModel, fileName: String) {
    var isPlaying by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    var progressAudio = vm.progressRatio
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = {
                isPlaying = !isPlaying
                Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show()
                vm.play(fileName)
            })
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Box {
            if (isPlaying) {
                CircularWavyProgressIndicator(color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = {
                    isPlaying = !isPlaying
                    vm.pause()
                }) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            } else {
                IconButton(onClick = { isPlaying = !isPlaying
                vm.play(fileName)}) {
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
                    progress = { progressAudio },
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

@Composable
fun Messages(vm: VoiceViewModel) {
    val voiceNotes = vm.audioList
    LazyColumn {
        items(voiceNotes) { i ->
            VoiceMsg(vm = vm, i.name)
        }
    }
}