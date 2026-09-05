package com.theveloper.pixelplay.presentation.components.scoped

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import com.theveloper.pixelplay.presentation.navigation.Screen
import com.theveloper.pixelplay.presentation.viewmodel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun PlayerEqualizerNavigationEffect(
    navController: NavHostController,
    sheetCollapsedTargetY: Float,
    sheetMotionController: SheetMotionController,
    playerViewModel: PlayerViewModel
) {
    LaunchedEffect(playerViewModel) {
        playerViewModel.equalizerNavigationRequests.collectLatest {
            sheetMotionController.snapCollapsed(sheetCollapsedTargetY)
            navController.navigate(Screen.Equalizer.route) {
                launchSingleTop = true
            }
        }
    }
}
