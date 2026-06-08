package app.bytejoseph.tunnelvoice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CircleNotifications
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.bytejoseph.tunnelvoice.VoiceViewModel
import app.bytejoseph.tunnelvoice.models.VoiceNotes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VoiceMessageItem(vm: VoiceViewModel, v: VoiceNotes, select: Int = 0) {
    val progressAudio = vm.progressRatio
    val isActuallyPlaying = vm.isPlaying && v.name == vm.currentFile

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = {
                // Potential action for clicking the row
            })
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Box {
            if (isActuallyPlaying) {
                CircularWavyProgressIndicator(color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = {
                    vm.pause()
                }) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(onClick = {
                    vm.play(v.name, select)
                }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
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
            if (isActuallyPlaying) {
                LinearWavyProgressIndicator(
                    progress = { progressAudio },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    amplitude = { 1.0f },
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LinearProgressIndicator(
                    progress = { if (v.name == vm.currentFile) progressAudio else 0f },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CircleNotifications,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    v.time12, fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
    }
}
