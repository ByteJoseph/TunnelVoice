package app.bytejoseph.tunnelvoice

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bytejoseph.tunnelvoice.models.VoiceNotes
import app.bytejoseph.tunnelvoice.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VoiceViewModel : ViewModel() {

    var has2Whatsapp by mutableStateOf(false)
        private set

    // Audio lists
    var audioList = mutableStateListOf<VoiceNotes>()
    var account1List = mutableStateListOf<VoiceNotes>()
    var account2List = mutableStateListOf<VoiceNotes>()

    // Playback state
    var isPlaying by mutableStateOf(false)
        private set

    // Compose state for playback progress (0.0f to 1.0f)
    var progressRatio by mutableStateOf(0f)
        private set
    var currentFile by mutableStateOf("empty")
        private set

    private var player: MediaPlayer? = null
    private var progressJob: Job? = null
    private lateinit var accounts: List<String>

    private val targetPath = Constants.WHATSAPP_VOICE_NOTES_PATH
    private val accountPath = Constants.WHATSAPP_ACCOUNTS_PATH

    private var lastFolder = "$targetPath/${getLastModifiedName(targetPath)}"
    private var acc1path = ""
    private var acc2path = ""

    init {
        refreshAudioFiles()
    }

    fun checkWhatsapp() {
        val dir = File(accountPath)

        if (!dir.exists() || !dir.isDirectory) {
            has2Whatsapp = false
            Log.d("CheckWhatsapp", "accounts folder missing")
            return
        }

        val children = dir.listFiles()
        has2Whatsapp = (children?.size == 2)

        if (has2Whatsapp) {
            accounts = children!!.map { it.name }
            val base1 = "$accountPath/${accounts[0]}/Media/WhatsApp Voice Notes"
            acc1path = "$base1/${getLastModifiedName(base1)}"
            val base2 = "$accountPath/${accounts[1]}/Media/WhatsApp Voice Notes"
            acc2path = "$base2/${getLastModifiedName(base2)}"
            Log.d("CheckWhatsapp", "Exactly 2 WhatsApp accounts detected")
        } else {
            Log.d("CheckWhatsapp", "WhatsApp accounts count != 2")
        }
    }

    private fun getTodayAndYesterdayStrings(): Pair<String, String> {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val todayStr = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = formatter.format(calendar.time)
        return Pair(todayStr, yesterdayStr)
    }

    private fun fetchVoiceNotesFromDir(dirPath: String): List<VoiceNotes> {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val files = dir.listFiles()?.filter { it.isFile && it.name != ".nomedia" }
            ?.sortedByDescending { it.lastModified() }
            ?: return emptyList()

        val formatterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatterTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val (todayStr, yesterdayStr) = getTodayAndYesterdayStrings()

        return files.map { file ->
            val lastMod = Date(file.lastModified())
            val dateStr = formatterDate.format(lastMod)
            val displayDate = when (dateStr) {
                todayStr -> "Today"
                yesterdayStr -> "Yesterday"
                else -> dateStr
            }
            VoiceNotes(
                name = file.name,
                date = displayDate,
                time12 = formatterTime.format(lastMod)
            )
        }
    }

    fun loadAudioFiles() {
        if (!has2Whatsapp) {
            val notes = fetchVoiceNotesFromDir(lastFolder)
            audioList.clear()
            audioList.addAll(notes)
        } else {
            val notes1 = fetchVoiceNotesFromDir(acc1path)
            account1List.clear()
            account1List.addAll(notes1)

            val notes2 = fetchVoiceNotesFromDir(acc2path)
            account2List.clear()
            account2List.addAll(notes2)
        }
    }

    fun refreshAudioFiles() {
        checkWhatsapp()
        lastFolder = "$targetPath/${getLastModifiedName(targetPath)}"
        loadAudioFiles()
    }

    fun play(fileName: String, select: Int = 0) {
        currentFile = fileName

        val folder = when (select) {
            1 -> acc1path
            2 -> acc2path
            else -> lastFolder
        }

        val filePath = "$folder/$fileName"
        stop()

        val file = File(filePath)
        if (!file.exists()) return

        val mp = MediaPlayer()
        try {
            mp.setDataSource(file.absolutePath)
            mp.setOnPreparedListener {
                it.start()
                isPlaying = true
                startProgressUpdates()
            }
            mp.setOnCompletionListener {
                stop()
            }
            mp.prepareAsync()
            player = mp
        } catch (e: Exception) {
            Log.e("VoiceViewModel", "Error playing audio", e)
            mp.release()
        }
    }

    fun pause() {
        player?.pause()
        isPlaying = false
    }

    fun resume() {
        player?.start()
        isPlaying = true
    }

    fun stop() {
        progressJob?.cancel()
        progressJob = null
        player?.release()
        player = null
        isPlaying = false
        progressRatio = 0f
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (player != null && player?.isPlaying == true && isActive) {
                val mp = player!!
                val pos = mp.currentPosition
                val dur = mp.duration
                progressRatio = if (dur > 0) pos.toFloat() / dur else 0f
                delay(50)
            }
        }
    }

    private fun getLastModifiedName(path: String): String {
        val dir = File(path)
        val lastModifiedFile = dir.listFiles()?.maxByOrNull { it.lastModified() }
        return lastModifiedFile?.name ?: ""
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}
