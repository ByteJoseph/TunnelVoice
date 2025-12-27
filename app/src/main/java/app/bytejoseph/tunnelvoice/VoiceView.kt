package app.bytejoseph.tunnelvoice

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Timer

class VoiceViewModel : ViewModel() {

    var has2Whatsapp by mutableStateOf(false)
        private set

    // Audio list
    var audioList = mutableStateListOf<VoiceNotes>()
    var account1List =  mutableStateListOf<VoiceNotes>()
    var account2List =  mutableStateListOf<VoiceNotes>()

    // Playback state
    var isPlaying by mutableStateOf(false)
        private set

    // Compose state for playback progress (0.0f to 1.0f)
    var progressRatio by mutableStateOf(0f)
        private set
    var currentFile by mutableStateOf("empty")
        private set

    private var player: MediaPlayer? = null
    private var progressTimer: Timer? = null
    private lateinit var accounts: List<String>;

    val targetPath =
        "/storage/emulated/0/Android/media/com.whatsapp/whatsapp/Media/WhatsApp Voice Notes"
    var accountPath = "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/accounts"
    var lastFolder = targetPath + "/" + getLastModifiedName(targetPath)
    var acc1path = ""
    var acc2path = ""

    init {
        checkWhatsapp()
        loadAudioFiles()

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
            var base = "$accountPath/${accounts[0]}/Media/WhatsApp Voice Notes"
            acc1path = "$base/${getLastModifiedName(base)}"
            base = "$accountPath/${accounts[1]}/Media/WhatsApp Voice Notes"
            acc2path = "$base/${getLastModifiedName(base)}"
            Log.d("CheckWhatsapp", "Exactly 2 WhatsApp accounts detected")
        } else {
            Log.d("CheckWhatsapp", "WhatsApp accounts count != 2")
        }
    }


    fun getTodayAndYesterday(): Pair<String, String> {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val todayStr = formatter.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = formatter.format(calendar.time)

        return Pair(todayStr, yesterdayStr)
    }

    /** Load WhatsApp voice notes from storage */
    public fun loadAudioFiles() {
        if (!has2Whatsapp) {
            val dir0 = File(lastFolder)
            if (!dir0.exists() || !dir0.isDirectory) return

            val files = dir0.listFiles()?.filter { it.isFile && it.name != ".nomedia" }
                ?.sortedByDescending { it.lastModified() } // sort by last modified, latest first
                ?: return

            audioList.clear()

            val formatterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatterTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val (todayStr, yesterdayStr) = getTodayAndYesterday()

            files.forEach { file ->
                val lastMod = Date(file.lastModified())
                val date = formatterDate.format(lastMod)
                if (date == todayStr) {
                    audioList.add(
                        VoiceNotes(
                            name = file.name, date = "Today", time12 = formatterTime.format(lastMod)
                        )
                    )
                } else if (date == yesterdayStr) {
                    audioList.add(
                        VoiceNotes(
                            name = file.name,
                            date = "Yesterday",
                            time12 = formatterTime.format(lastMod)
                        )
                    )
                } else {
                    audioList.add(
                        VoiceNotes(
                            name = file.name,
                            date = date,
                            time12 = formatterTime.format(lastMod)
                        )
                    )
                }
            }
        } else {
            val dir1 = File(acc1path)
            if (!dir1.exists() || !dir1.isDirectory) return
            val dir2 = File(acc2path)
            if (!dir2.exists() || !dir2.isDirectory) return
            val files1 = dir1.listFiles()?.filter { it.isFile && it.name != ".nomedia" }
                ?.sortedByDescending { it.lastModified() } // sort by last modified, latest first
                ?: return
            val files2 = dir2.listFiles()?.filter { it.isFile && it.name != ".nomedia" }
                ?.sortedByDescending { it.lastModified() } // sort by last modified, latest first
                ?: return
            account1List.clear()
            account2List.clear()
            val formatterDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatterTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val (todayStr, yesterdayStr) = getTodayAndYesterday()
            files1.forEach { file ->
                val lastMod = Date(file.lastModified())
                val date = formatterDate.format(lastMod)
                if (date == todayStr) {
                    account1List.add(
                        VoiceNotes(
                            name = file.name, date = "Today", time12 = formatterTime.format(lastMod)
                        )
                    )
                } else if (date == yesterdayStr) {
                    account1List.add(
                        VoiceNotes(
                            name = file.name,
                            date = "Yesterday",
                            time12 = formatterTime.format(lastMod)
                        )
                    )
                } else {
                    account1List.add(
                        VoiceNotes(
                            name = file.name,
                            date = date,
                            time12 = formatterTime.format(lastMod)
                        )
                    )
                }
            }
            files2.forEach { file ->
                val lastMod = Date(file.lastModified())
                val date = formatterDate.format(lastMod)
                if (date == todayStr) {
                    account2List.add(
                        VoiceNotes(
                            name = file.name, date = "Today", time12 = formatterTime.format(lastMod)
                        )
                    )
                } else if (date == yesterdayStr) {
                    account2List.add(
                        VoiceNotes(
                            name = file.name,
                            date = "Yesterday",
                            time12 = formatterTime.format(lastMod)
                        )
                    )
                } else {
                    account2List.add(
                        VoiceNotes(
                            name = file.name,
                            date = date,
                            time12 = formatterTime.format(lastMod)
                        )
                    )
                }
            }
        }
    }

    /** Play an audio file */
    fun play(filePath: String,select: Int = 0) {
        currentFile = filePath

        if (select == 1){
          lastFolder = acc1path
        }
        else if (select==2){
           lastFolder = acc2path
        }
        val filePath = "$lastFolder/$filePath"
        stop() // stop previous playback

        val file = File(filePath)
        if (!file.exists()) return
//        throw RuntimeException("Test Crash")
        player = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            setOnPreparedListener { mp ->
                mp.start()
                startProgressUpdates()
            }
            setOnCompletionListener {
                currentFile = "none"
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

    /** Updates progressRatio every 50ms */
    private fun startProgressUpdates() {
        viewModelScope.launch {
            while (player != null && player!!.isPlaying && isActive) {
                val mp = player!!
                val pos = mp.currentPosition
                val dur = mp.duration

                // Safe update on main thread
                progressRatio = if (dur > 0) pos.toFloat() / dur else 0f

                delay(50) // 50ms update interval
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