package app.bytejoseph.tunnelvoice.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.bytejoseph.tunnelvoice.VoiceViewModel
import app.bytejoseph.tunnelvoice.models.TabItem

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!"
    )
}

@Composable
fun MainScreen(vm: VoiceViewModel, tabItems: List<TabItem>) {
    if (!vm.has2Whatsapp) {
        MessageList(vm = vm)
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
                MessageListForPager(vm = vm, key = index)
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
