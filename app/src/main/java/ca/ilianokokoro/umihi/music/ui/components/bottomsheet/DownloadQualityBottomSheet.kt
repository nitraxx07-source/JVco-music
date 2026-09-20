package ca.ilianokokoro.umihi.music.ui.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import ca.ilianokokoro.umihi.music.models.DownloadQuality
import ca.ilianokokoro.umihi.music.ui.components.SheetHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadQualityBottomSheet(
    selected: DownloadQuality,
    onSelect: (DownloadQuality) -> Unit,
    onClose: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SheetHeader(
                icon = Icons.Outlined.CloudDownload,
                title = "Calidad de descarga",
                modifier = Modifier.fillMaxWidth(),
            )
            Column(modifier = Modifier.selectableGroup()) {
                DownloadQuality.entries.forEach { quality ->
                    val label = when (quality) {
                        DownloadQuality.LOW -> "Baja"
                        DownloadQuality.MEDIUM -> "Media"
                        DownloadQuality.HIGH -> "Alta"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .selectable(
                                selected = selected == quality,
                                onClick = { onSelect(quality); onClose() },
                                role = Role.RadioButton,
                            ).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = selected == quality, onClick = null)
                        Text(label, modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
        }
    }
}