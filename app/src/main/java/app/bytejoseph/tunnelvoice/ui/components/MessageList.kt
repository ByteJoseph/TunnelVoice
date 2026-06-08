package app.bytejoseph.tunnelvoice.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import app.bytejoseph.tunnelvoice.VoiceViewModel

@Composable
fun MessageList(vm: VoiceViewModel) {
    val grouped = remember(vm.audioList) {
        vm.audioList.groupBy { it.date }
    }

    LazyColumn(
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
    val grouped = remember(vm.account1List, vm.account2List, key) {
        if (key == 1) {
            vm.account2List.groupBy { it.date }
        } else {
            vm.account1List.groupBy { it.date }
        }
    }

    LazyColumn(
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
