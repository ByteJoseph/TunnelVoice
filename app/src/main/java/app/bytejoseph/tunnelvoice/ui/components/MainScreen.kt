package app.bytejoseph.tunnelvoice.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.bytejoseph.tunnelvoice.VoiceViewModel
import app.bytejoseph.tunnelvoice.models.TabItem
import kotlinx.coroutines.launch

@Composable
fun MainScreen(vm: VoiceViewModel, tabItems: List<TabItem>, modifier: Modifier = Modifier) {
    if (!vm.has2Whatsapp) {
        Column(modifier = modifier.fillMaxSize()) {
            MessageList(vm = vm)
        }
    } else {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val pagerState = rememberPagerState {
            tabItems.size
        }
        val scope = rememberCoroutineScope()

        LaunchedEffect(pagerState.currentPage) {
            selectedIndex = pagerState.currentPage
        }
        Column(modifier = modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { index ->
                MessageListForPager(vm = vm, key = index)
            }
            NavigationBar {
                tabItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        label = { Text(item.title) },
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
