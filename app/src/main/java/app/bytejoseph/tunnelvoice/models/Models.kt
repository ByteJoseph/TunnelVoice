package app.bytejoseph.tunnelvoice.models

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class VoiceNotes(
    val name: String,
    val date: String,
    val time12: String
)

@Immutable
data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
