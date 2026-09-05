package com.theveloper.pixelplay.presentation.screens.equalizer

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theveloper.pixelplay.R
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun BandSlidersSection(
    bandLevels: List<Int>,
    isEnabled: Boolean,
    currentPreset: EqualizerPreset,
    editingPresetName: String?,
    onBandLevelChanged: (Int, Int) -> Unit,
    viewMode: EqualizerViewMode,
    onSaveClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onUnpinClick: () -> Unit,
    onPresetsListClick: () -> Unit
) {
    val frequencies = EqualizerPreset.BAND_FREQUENCIES
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                 val isCustomOrSaved = editingPresetName != null || currentPreset.name == "custom" || currentPreset.isCustom
                 val displayLabel = editingPresetName ?: currentPreset.displayName
                 
                 Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape,
                    onClick = onPresetsListClick,
                    enabled = isCustomOrSaved 
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (isCustomOrSaved) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.ExpandMore,
                                contentDescription = stringResource(R.string.equalizer_presets_cd),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                
                if (currentPreset.name == "custom" && editingPresetName == null) {
                     Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape,
                        onClick = onSaveClick
                    ) {
                          Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Icon(
                                 imageVector = Icons.Rounded.Save,
                                 contentDescription = null,
                                 modifier = Modifier.size(20.dp),
                                 tint = MaterialTheme.colorScheme.onTertiaryContainer
                             )
                             Spacer(modifier = Modifier.width(6.dp))
                             Text(
                                 text = stringResource(R.string.common_save),
                                 color = MaterialTheme.colorScheme.onTertiaryContainer,
                                 fontWeight = FontWeight.Bold
                             )
                          }
                     }
                }
                
                if (editingPresetName != null) {
                    // Update Option
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        onClick = onUpdateClick
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.equalizer_action_update),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Save New Option
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape,
                        onClick = onSaveClick
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.equalizer_action_save_new),
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            when (viewMode) {
                EqualizerViewMode.GRAPH -> {
                    GraphBandSliders(
                        bandLevels = bandLevels,
                        isEnabled = isEnabled,
                        frequencies = frequencies,
                        onBandLevelChanged = onBandLevelChanged
                    )
                }
                EqualizerViewMode.HYBRID -> {
                    HybridBandSliders(
                        bandLevels = bandLevels,
                        isEnabled = isEnabled,
                        frequencies = frequencies,
                        onBandLevelChanged = onBandLevelChanged
                    )
                }
                EqualizerViewMode.SLIDERS -> {
                    val pagerState = rememberPagerState(pageCount = { 2 })

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val start = page * 5
                            val end = minOf(start + 5, bandLevels.size)

                            for (index in start until end) {
                                val level = bandLevels.getOrElse(index) { 0 }
                                VerticalBandSlider(
                                    frequency = frequencies.getOrElse(index) { "${index * 1000}Hz" },
                                    level = level,
                                    isEnabled = isEnabled,
                                    onLevelChanged = { newLevel -> onBandLevelChanged(index, newLevel) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Page Indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                             val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(if (pagerState.currentPage == iteration) 10.dp else 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun GraphBandSliders(
    bandLevels: List<Int>,
    isEnabled: Boolean,
    frequencies: List<String>,
    onBandLevelChanged: (Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp) // Slightly taller for graph headroom
    ) {
        val density = LocalDensity.current
        
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            bandLevels.forEachIndexed { index, level ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Value Text (Top)
                    Text(
                        text = if (level > 0) "+$level" else "$level",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(20.dp)
                    )
                    

                    CustomVerticalSlider(
                        value = level.toFloat(),
                        onValueChange = { onBandLevelChanged(index, it.roundToInt()) },
                        valueRange = -15f..15f,
                        enabled = isEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        activeTrackColor = Color.Transparent, 
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                        thumbColor = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        trackThickness = 4.dp, 
                        thumbSize = 16.dp,
                        thumbShape = CircleShape
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Frequency
                    Text(
                        text = frequencies.getOrElse(index) { "" }.replace("Hz", "").replace("k", "k"),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Graph Canvas Overlay
        if (isEnabled) {
            val primaryColor = MaterialTheme.colorScheme.primary
            
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                val widthPerBand = size.width / bandLevels.size
                val path = Path()
                
                // Constants from CustomVerticalSlider
                val sliderTopPadding = 20.dp.toPx()
                val sliderBottomPadding = 24.dp.toPx()
                val thumbSize = 16.dp.toPx() // Updated to 16dp
                val verticalPadding = 4.dp.toPx()
                val availableHeight = size.height - sliderTopPadding - sliderBottomPadding
                val trackHeight = availableHeight - thumbSize - (verticalPadding * 2)
                val topOffset = sliderTopPadding + verticalPadding + (thumbSize / 2)
                
                val points = bandLevels.mapIndexed { index, level ->
                    val x = (widthPerBand * index) + (widthPerBand / 2)
                    val normalized = ((level - (-15f)) / (15f - -15f)).coerceIn(0f, 1f)
                    val yNormalized = 1f - normalized
                    val y = topOffset + (yNormalized * trackHeight)
                    Offset(x, y)
                }
                
                if (points.isNotEmpty()) {
                    path.moveTo(points[0].x, points[0].y)
                    
                    for (i in 0 until points.size - 1) {
                        val p0 = points[maxOf(0, i - 1)]
                        val p1 = points[i]
                        val p2 = points[i + 1]
                        val p3 = points[minOf(points.size - 1, i + 2)]
                        
                        val cp1X = p1.x + (p2.x - p0.x) * 0.2f
                        val cp1Y = p1.y + (p2.y - p0.y) * 0.2f
                        val cp2X = p2.x - (p3.x - p1.x) * 0.2f
                        val cp2Y = p2.y - (p3.y - p1.y) * 0.2f
                        
                        path.cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
                    }
                    
                    // Draw Line
                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Draw Fill
                    val fillPath = Path()
                    fillPath.addPath(path)
                    fillPath.lineTo(points.last().x, size.height - sliderBottomPadding)
                    fillPath.lineTo(points.first().x, size.height - sliderBottomPadding)
                    fillPath.close()
                    
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height
                        )
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun VerticalBandSlider(
    frequency: String,
    level: Int,
    isEnabled: Boolean,
    onLevelChanged: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .fillMaxHeight()
    ) {
        // Level indicator
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (isEnabled) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHighest
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (level >= 0) "+$level" else "$level",
                style = MaterialTheme.typography.labelSmall,
                color = if (isEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Custom vertical slider
        CustomVerticalSlider(
            value = level.toFloat(),
            onValueChange = { onLevelChanged(it.roundToInt()) },
            valueRange = -15f..15f,
            enabled = isEnabled,
            modifier = Modifier
                .weight(1f)
                .width(40.dp),
            activeTrackColor = if (isEnabled) MaterialTheme.colorScheme.primary 
                              else MaterialTheme.colorScheme.onSurfaceVariant,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            thumbColor = if (isEnabled) MaterialTheme.colorScheme.onPrimary // Contrast for thick slider
                        else MaterialTheme.colorScheme.onSurfaceVariant
            // Default params used: trackThickness = Unspecified (fill), thumbSize = 24.dp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Frequency label
        Text(
            text = frequency,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



// ... imports ...


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun CustomVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    thumbColor: Color,
    trackThickness: androidx.compose.ui.unit.Dp = androidx.compose.ui.unit.Dp.Unspecified,
    thumbSize: androidx.compose.ui.unit.Dp = 24.dp,
    thumbShape: androidx.compose.ui.graphics.Shape? = null
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val view = LocalView.current
    val thumbSizePx = with(density) { thumbSize.toPx() }
    val thumbRadiusPx = thumbSizePx / 2
    
    // Geometry correction: Adding padding so thumb doesn't touch the absolute container edges
    val verticalPaddingDp = 4.dp
    val verticalPaddingPx = with(density) { verticalPaddingDp.toPx() }
    
    // Normalize value to 0..1 range
    val normalizedValue = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    
    // Track previous integer value for haptic feedback
    var lastHapticValue by remember { mutableIntStateOf(value.roundToInt()) }
    var isInteracting by remember { mutableStateOf(false) }
    var dragNormalizedValue by remember { mutableFloatStateOf(normalizedValue) }

    LaunchedEffect(normalizedValue, isInteracting) {
        if (!isInteracting) {
            dragNormalizedValue = normalizedValue
        }
    }
    
    // Create the Path
    val starShape = remember { com.theveloper.pixelplay.utils.shapes.RoundedStarShape(sides = 8, curve = 0.1) }
    val finalShape = thumbShape ?: starShape
    
    val thumbPath = remember(thumbSizePx, finalShape) {
        val outline = finalShape.createOutline(
            androidx.compose.ui.geometry.Size(thumbSizePx, thumbSizePx),
            androidx.compose.ui.unit.LayoutDirection.Ltr,
            density
        )
        when (outline) {
            is androidx.compose.ui.graphics.Outline.Generic -> outline.path
            is androidx.compose.ui.graphics.Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
            is androidx.compose.ui.graphics.Outline.Rectangle -> Path().apply { addRect(outline.rect) }
        }
    }

    // Colors for "inside" look
    val actualActiveTrackColor = if (enabled) activeTrackColor else activeTrackColor.copy(alpha = 0.3f)
    val actualInactiveTrackColor = inactiveTrackColor
    val actualThumbColor = if (enabled) thumbColor else MaterialTheme.colorScheme.onSurfaceVariant

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val heightPx = with(density) { maxHeight.toPx() }
        
        // Usable track height (center of thumb travels within this range, respecting padding)
        val trackHeight = heightPx - thumbSizePx - (verticalPaddingPx * 2)
        val safeTrackHeight = trackHeight.coerceAtLeast(1f)
        val displayNormalizedValue = if (isInteracting) dragNormalizedValue else normalizedValue
        
        // thumb Y position (center)
        val thumbCenterY = heightPx - verticalPaddingPx - thumbRadiusPx - (displayNormalizedValue * safeTrackHeight)
        
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .pointerInput(enabled, valueRange.start, valueRange.endInclusive, safeTrackHeight, heightPx) {
                    if (!enabled) return@pointerInput
                    fun dispatchValue(touchY: Float, forceHaptic: Boolean = false) {
                        val trackTopY = verticalPaddingPx + thumbRadiusPx
                        val relativeY = (touchY - trackTopY).coerceIn(0f, safeTrackHeight)
                        val newNormalized = 1f - (relativeY / safeTrackHeight)
                        dragNormalizedValue = newNormalized
                        val newValue = valueRange.start + newNormalized * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)

                        val newInt = newValue.roundToInt()
                        if (forceHaptic || newInt != lastHapticValue) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            lastHapticValue = newInt
                        }
                    }

                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        view.parent?.requestDisallowInterceptTouchEvent(true)
                        isInteracting = true
                        down.consume()
                        dispatchValue(down.position.y, forceHaptic = true)

                        var activePointerId = down.id
                        while (true) {
                            val event = awaitPointerEvent()
                            val pointerChange = event.changes.firstOrNull { it.id == activePointerId }
                                ?: event.changes.firstOrNull { it.pressed }?.also { activePointerId = it.id }
                                ?: break

                            if (!pointerChange.pressed) {
                                pointerChange.consume()
                                break
                            }

                            if (pointerChange.position.y != pointerChange.previousPosition.y) {
                                pointerChange.consume()
                                dispatchValue(pointerChange.position.y)
                            }
                        }

                        isInteracting = false
                        view.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
        ) {
            val centerX = size.width / 2
            
            // Determine track drawing width
            val trackWidth = if (trackThickness != androidx.compose.ui.unit.Dp.Unspecified) {
                with(density) { trackThickness.toPx() }
            } else {
                size.width
            }
            // If explicit thickness, center it. If fill, left is 0.
            val trackLeft = if (trackThickness != androidx.compose.ui.unit.Dp.Unspecified) {
                centerX - (trackWidth / 2)
            } else {
                0f
            }
            
            // 1. Draw Inactive Track
            drawRoundRect(
                color = actualInactiveTrackColor,
                topLeft = androidx.compose.ui.geometry.Offset(trackLeft, 0f), 
                size = androidx.compose.ui.geometry.Size(trackWidth, size.height), // Use height not size.width
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(trackWidth / 2)
            )
            
            // 2. Draw Active Track
            // Cap at thumb center
            drawCircle(
                color = actualActiveTrackColor,
                radius = trackWidth / 2, // Use trackWidth
                center = androidx.compose.ui.geometry.Offset(centerX, thumbCenterY)
            )
            
            val activeRectTop = thumbCenterY
            val activeRectHeight = size.height - activeRectTop
            
            if (activeRectHeight > 0f) {
                val activeTrackPath = androidx.compose.ui.graphics.Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            rect = androidx.compose.ui.geometry.Rect(
                                offset = androidx.compose.ui.geometry.Offset(trackLeft, activeRectTop),
                                size = androidx.compose.ui.geometry.Size(trackWidth, activeRectHeight)
                            ),
                            topLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
                            topRight = androidx.compose.ui.geometry.CornerRadius.Zero,
                            bottomLeft = androidx.compose.ui.geometry.CornerRadius(trackWidth / 2),
                            bottomRight = androidx.compose.ui.geometry.CornerRadius(trackWidth / 2)
                        )
                    )
                }
                drawPath(
                    path = activeTrackPath,
                    color = actualActiveTrackColor
                )
            }

            // 3. Draw Thumb
            translate(
                left = centerX - thumbRadiusPx, 
                top = thumbCenterY - thumbRadiusPx
            ) {
                // Rotate thumb based on normalized value (0 at bottom -> 360 at top)
                rotate(
                    degrees = displayNormalizedValue * 360f,
                    pivot = androidx.compose.ui.geometry.Offset(thumbRadiusPx, thumbRadiusPx)
                ) {
                    drawPath(
                        path = thumbPath,
                        color = actualThumbColor
                    )
                }
            }
        }
    }
}

@Composable
