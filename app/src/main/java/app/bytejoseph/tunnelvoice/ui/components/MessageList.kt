package app.bytejoseph.tunnelvoice.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.bytejoseph.tunnelvoice.VoiceViewModel

@Composable
fun MessageList(vm: VoiceViewModel) {
    if (vm.audioList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No voice notes found.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    val grouped by remember {
        derivedStateOf { vm.audioList.groupBy { it.date } }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        grouped.forEach { (date, itemsForDate) ->
            item(key = "date-$date") {
                DateLabel(date)
            }
            items(items = itemsForDate, key = { it.name }) { v ->
                VoiceMessageItem(vm = vm, v)
            }
        }
    }
}

@Composable
fun MessageListForPager(vm: VoiceViewModel, key: Int) {
    val currentList = if (key == 1) vm.account2List else vm.account1List
    val otherList = if (key == 1) vm.account1List else vm.account2List

    if (currentList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val message = if (otherList.isNotEmpty()) {
                "No voice notes here.\nTry swiping or clicking the other account."
            } else {
                "No voice notes found."
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    val grouped by remember(key) {
        derivedStateOf { currentList.groupBy { it.date } }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        grouped.forEach { (date, itemsForDate) ->
            item(key = "date-$date") {
                DateLabel(date)
            }
            items(items = itemsForDate, key = { it.name }) { v ->
                VoiceMessageItem(vm = vm, v, select = key + 1)
            }
        }
    }
}
