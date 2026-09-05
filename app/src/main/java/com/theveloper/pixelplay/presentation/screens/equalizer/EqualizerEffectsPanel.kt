package com.theveloper.pixelplay.presentation.screens.equalizer

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theveloper.pixelplay.R
import kotlin.math.roundToInt

@Composable
fun EffectControlsSection(
    bassBoostEnabled: Boolean,
    bassBoostStrength: Float,
    virtualizerEnabled: Boolean,
    virtualizerStrength: Float,
    loudnessEnabled: Boolean,
    loudnessStrength: Float,
    isBassBoostSupported: Boolean,
    isVirtualizerSupported: Boolean,
    isLoudnessEnhancerSupported: Boolean,
    isBassBoostDismissed: Boolean = false,
    isVirtualizerDismissed: Boolean = false,
    isLoudnessDismissed: Boolean = false,
    onBassBoostEnabledChange: (Boolean) -> Unit,
    onBassBoostStrengthChange: (Float) -> Unit,
    onVirtualizerEnabledChange: (Boolean) -> Unit,
    onVirtualizerStrengthChange: (Float) -> Unit,
    onLoudnessEnabledChange: (Boolean) -> Unit,
    onLoudnessStrengthChange: (Float) -> Unit,
    onDismissBassBoost: () -> Unit,
    onDismissVirtualizer: () -> Unit,
    onDismissLoudness: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .height(IntrinsicSize.Max)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isBassBoostSupported) {
            EffectCard(
                title = stringResource(R.string.equalizer_bass_boost),
                value = bassBoostStrength,
                valueRange = 0f..1000f,
                isEnabled = bassBoostEnabled,
                onValueChange = onBassBoostStrengthChange,
                onEnabledChange = onBassBoostEnabledChange
            )
        } else if (!isBassBoostDismissed) {
            UnsupportedEffectCard(
                title = stringResource(R.string.equalizer_bass_boost),
                onDismiss = onDismissBassBoost
            )
        }

        if (isVirtualizerSupported) {
            EffectCard(
                title = stringResource(R.string.equalizer_virtualizer),
                value = virtualizerStrength,
                valueRange = 0f..1000f,
                isEnabled = virtualizerEnabled,
                onValueChange = onVirtualizerStrengthChange,
                onEnabledChange = onVirtualizerEnabledChange
            )
        } else if (!isVirtualizerDismissed) {
            UnsupportedEffectCard(
                title = stringResource(R.string.equalizer_virtualizer),
                onDismiss = onDismissVirtualizer
            )
        }

        if (isLoudnessEnhancerSupported) {
            EffectCard(
                title = stringResource(R.string.equalizer_loudness),
                value = loudnessStrength,
                valueRange = 0f..1000f,
                isEnabled = loudnessEnabled,
                onValueChange = onLoudnessStrengthChange,
                onEnabledChange = onLoudnessEnabledChange
            )
        } else if (!isLoudnessDismissed) {
            UnsupportedEffectCard(
                title = stringResource(R.string.equalizer_loudness),
                onDismiss = onDismissLoudness
            )
        }
    }
}

@Composable
private fun EffectCard(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    isEnabled: Boolean,
    onValueChange: (Float) -> Unit,
    onEnabledChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(150.dp)
                    .height(110.dp)
            ) {
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    enabled = isEnabled,
                    modifier = Modifier
                        .rotate(-90f)
                        .width(100.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledThumbColor = MaterialTheme.colorScheme.outlineVariant,
                        disabledActiveTrackColor = MaterialTheme.colorScheme.outlineVariant,
                        disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                )
            }

            Text(
                text = "${(value / 10f).roundToInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                FilledIconToggleButton(
                    checked = isEnabled,
                    onCheckedChange = onEnabledChange,
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.outline,
                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                        checkedContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PowerSettingsNew,
                        contentDescription = "Toggle $title",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun UnsupportedEffectCard(
    title: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(16.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.equalizer_effect_not_supported_device),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
