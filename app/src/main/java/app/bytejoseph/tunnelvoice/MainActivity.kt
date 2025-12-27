package app.bytejoseph.tunnelvoice

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.ExposurePlus2
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CircleNotifications
import androidx.compose.material.icons.outlined.ExposurePlus1
import androidx.compose.material.icons.outlined.ExposurePlus2
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PlusOne
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.bytejoseph.tunnelvoice.ui.theme.TunnelVoiceTheme
import com.google.firebase.auth.FirebaseAuth

@Immutable
data class VoiceNotes(
    val name: String, val date: String, val time12: String
)

@Immutable
data class TabItem(
    val title: String, val unselectedIcon: ImageVector, val selectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    private val voiceViewModel: VoiceViewModel by viewModels()
    val tabItems = listOf(
        TabItem(
            title = "Account",
            unselectedIcon = Icons.Outlined.ExposurePlus1,
            selectedIcon = Icons.Filled.ExposurePlus1
        ), TabItem(
            title = "Account",
            unselectedIcon = Icons.Outlined.ExposurePlus2,
            selectedIcon = Icons.Filled.ExposurePlus2
        )
    )
    private lateinit var auth: FirebaseAuth

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
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(
                            name = "Android"
                        )
                        MainView(vm = voiceViewModel, tabItems = tabItems)


                    }

                }
            }
        }
        auth = FirebaseAuth.getInstance()


        askFullFilePermission(this)
        fun signInAnonymously() {
            auth.signInAnonymously().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("AUTH", "Signed in: ${user?.uid}")
                } else {
                    Log.e("AUTH", "Sign-in failed", task.exception)
                }
            }
        }
        signInAnonymously()

    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!"
    )
}

@Composable
fun MainView(vm: VoiceViewModel, tabItems: List<TabItem>) {
//    vm.checkWhatsapp()
    var lisKey = 0
    if (!vm.has2Whatsapp) {
        Messages(vm = vm)
        lisKey = 1
    } else {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState {
            tabItems.size
        }
        LaunchedEffect(selectedIndex) {
            pagerState.animateScrollToPage(selectedIndex)
        }
        LaunchedEffect(pagerState.currentPage) {
            selectedIndex = pagerState.currentPage
        }
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { index ->
                Messages4Pager(vm = vm, key = index)
            }
            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index },
                        text = { Text(item.title) },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedIndex) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                }, contentDescription = item.title
                            )
                        })
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VoiceMsg(vm: VoiceViewModel, v: VoiceNotes,select :Int = 0) {
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
//                isPlaying = !isPlaying
//                Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show()
//                vm.play(fileName)
            })
            .height(60.dp)
            .fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .width(5.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
                .width(10.dp)
        )
        Box {
            if (isPlaying && v.name == vm.currentFile) {
//                CircularWavyProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                IconButton(onClick = {
                    isPlaying = !isPlaying
                    vm.play(v.name,select)
                }) {
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
           if (isPlaying)
            //            {
//                LinearWavyProgressIndicator(
//                    progress = { if (v.name == vm.currentFile) progressAudio else 0f },
//                    modifier = Modifier
//                        .align(Alignment.Center)
//                        .fillMaxWidth(),
//                    amplitude = { 1.0f },
//                    color = MaterialTheme.colorScheme.primary
//                )
//            } else
                        {
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

    // Group only when audioList changes
    val grouped = remember(vm.audioList) {
        vm.audioList.groupBy { it.date }
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        grouped.forEach { (date, itemsForDate) ->

            // Date Header
            item(key = "date-$date") {
                DateLabel(date)
            }

            // Messages for that date
            items(items = itemsForDate, key = { it.name }   // only if "name" is unique
            ) { v ->
                VoiceMsg(vm = vm, v)
            }
        }
    }
}

@Composable
fun Messages4Pager(vm: VoiceViewModel, key: Int) {

    vm.loadAudioFiles()
    var grouped = remember(vm.audioList) {
        vm.account1List.groupBy { it.date }
    }
    if (key == 1) {
        grouped = remember(vm.audioList) {
            vm.account2List.groupBy { it.date }
        }
    }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        grouped.forEach { (date, itemsForDate) ->

            // Date Header
            item(key = "date-$date") {
                DateLabel(date)
            }

            // Messages for that date
            items(items = itemsForDate, key = { it.name }   // only if "name" is unique
            ) { v ->
                VoiceMsg(vm = vm, v,key+1)
            }
        }
    }
}
