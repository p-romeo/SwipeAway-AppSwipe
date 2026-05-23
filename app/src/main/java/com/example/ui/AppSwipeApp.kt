package com.example.ui

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SwipeRecord
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.input.pointer.util.VelocityTracker

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.utils.PermissionUtils

// Navigation tabs
enum class AppTab(val title: String, val icon: ImageVector) {
    SWIPE("Swipe", Icons.Default.Favorite),
    QUEUE("Queue", Icons.Default.Delete),
    INSIGHTS("Insights", Icons.Default.Info),
    SETTINGS("Settings", Icons.Default.Settings)
}

// Swipe directions
enum class SwipeDirection { NONE, LEFT, RIGHT }

@Composable
fun AppIconView(
    packageName: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
        },
        update = { imageView ->
            try {
                imageView.setImageDrawable(imageView.context.packageManager.getApplicationIcon(packageName))
            } catch (e: PackageManager.NameNotFoundException) {
                imageView.setImageDrawable(null)
            }
        }
    )
}

@Composable
fun SwipeAwayLogo(modifier: Modifier = Modifier, size: androidx.compose.ui.unit.Dp = 32.dp) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        
        // Scale and stroke metrics relative to a 108dp base grid
        val speedStrokeWidth = (3.2f / 108f) * w
        val chevronStrokeWidth = (11f / 108f) * w
        
        fun scaleX(x: Float): Float = (x / 108f) * w
        fun scaleY(y: Float): Float = (y / 108f) * h

        // Speed Line 1 (Cyan)
        drawLine(
            color = Color(0xFF18D9E6),
            start = androidx.compose.ui.geometry.Offset(scaleX(29f), scaleY(44f)),
            end = androidx.compose.ui.geometry.Offset(scaleX(35f), scaleY(44f)),
            strokeWidth = speedStrokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Speed Line 2 (Electric Blue)
        drawLine(
            color = Color(0xFF1B5CFF),
            start = androidx.compose.ui.geometry.Offset(scaleX(32f), scaleY(49f)),
            end = androidx.compose.ui.geometry.Offset(scaleX(43f), scaleY(49f)),
            strokeWidth = speedStrokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Speed Line 3 (Hot Pink)
        drawLine(
            color = Color(0xFFFF0A7A),
            start = androidx.compose.ui.geometry.Offset(scaleX(25f), scaleY(54f)),
            end = androidx.compose.ui.geometry.Offset(scaleX(29f), scaleY(54f)),
            strokeWidth = speedStrokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Speed Line 4 (Electric Blue)
        drawLine(
            color = Color(0xFF1B5CFF),
            start = androidx.compose.ui.geometry.Offset(scaleX(32f), scaleY(59f)),
            end = androidx.compose.ui.geometry.Offset(scaleX(42f), scaleY(59f)),
            strokeWidth = speedStrokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Chevron Upper Branch (Electric Blue)
        drawLine(
            color = Color(0xFF1B5CFF),
            start = androidx.compose.ui.geometry.Offset(scaleX(50f), scaleY(34f)),
            end = androidx.compose.ui.geometry.Offset(scaleX(69f), scaleY(53f)),
            strokeWidth = chevronStrokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Chevron Lower Branch (Hot Pink)
        drawLine(
            color = Color(0xFFFF0A7A),
            start = androidx.compose.ui.geometry.Offset(scaleX(50f), scaleY(74f)),
            end = androidx.compose.ui.geometry.Offset(scaleX(69f), scaleY(55f)),
            strokeWidth = chevronStrokeWidth,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Chevron Forward Curve Hook (Bright Cyan)
        val hookPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(scaleX(66f), scaleY(42f))
            lineTo(scaleX(79f), scaleY(54f))
            lineTo(scaleX(66f), scaleY(66f))
        }
        drawPath(
            path = hookPath,
            color = Color(0xFF18D9E6),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = chevronStrokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )
    }
}

@Composable
fun AppSwipeApp(viewModel: SwipeViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var currentTab by remember { mutableStateOf(AppTab.SWIPE) }
    
    // Synchronize apps automatically when app is resumed/foregrounded
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.syncApps()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Listen to shared flow notifications
    LaunchedEffect(viewModel) {
        viewModel.undoMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.uninstallStatus.collectLatest { status ->
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                AppTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 300),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    AppTab.SWIPE -> SwipeScreen(viewModel)
                    AppTab.QUEUE -> QueueScreen(viewModel)
                    AppTab.INSIGHTS -> InsightsScreen(viewModel)
                    AppTab.SETTINGS -> SettingsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun SwipeScreen(viewModel: SwipeViewModel) {
    val pendingList by viewModel.pendingApps.collectAsStateWithLifecycle()
    val stats by viewModel.storageStatistics.collectAsStateWithLifecycle()
    val isRightSwipeUninstall by viewModel.isRightSwipeUninstall.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var isPermissionGranted by remember { mutableStateOf(true) }
    var swipeSignal by remember { mutableStateOf(SwipeDirection.NONE) }
    var flyingOutApp by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        isPermissionGranted = PermissionUtils.isUsageAccessGranted(context)
    }

    DisposableEffect(Unit) {
        isPermissionGranted = PermissionUtils.isUsageAccessGranted(context)
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isPermissionGranted) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("usage_permission_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Usage Permission",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Unlock Usage Statistics",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Authorize permission to sort items by actual days since last used.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Button(
                        onClick = { PermissionUtils.launchUsageAccessSettings(context) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_enable_usage_settings"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Enable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        // Upper stats banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SwipeAwayLogo(size = 36.dp)
                Column {
                    Text(
                        text = "SwipeAway",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            // Queue counter badge
            Surface(
                color = if (stats.queuedCount > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${stats.queuedCount} in Queue (${formatSize(stats.queuedStorageMb)})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (stats.queuedCount > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Active sorting card stack
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (pendingList.isNotEmpty()) {
                // Background shadow cards for Depth
                val cardsToShow = pendingList.take(3).reversed()
                cardsToShow.forEachIndexed { index, app ->
                    val isTop = index == cardsToShow.size - 1
                    val isSecond = index == cardsToShow.size - 2
                    val topAppIsFlying = flyingOutApp == cardsToShow.last().packageName

                    val distance = if (topAppIsFlying && !isTop) {
                        (cardsToShow.size - 1 - index) - 1
                    } else {
                        cardsToShow.size - 1 - index
                    }.coerceAtLeast(0)

                    val targetStackOffset = distance * 8f
                    val targetScale = 1f - (distance * 0.03f)
                    
                    val animatedStackOffset by animateFloatAsState(
                        targetValue = targetStackOffset,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "stackOffset"
                    )
                    val animatedScale by animateFloatAsState(
                        targetValue = targetScale,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "scale"
                    )
                    
                    val enabled = isTop || (isSecond && topAppIsFlying)
                    
                    key(app.packageName) {
                        SwipeCard(
                            app = app,
                            enabled = enabled,
                            scale = animatedScale,
                            offsetYOffset = animatedStackOffset.dp,
                            swipeSignal = if (isTop) swipeSignal else SwipeDirection.NONE,
                            onSwipeStart = {
                                flyingOutApp = app.packageName
                            },
                            onSwipeLeft = { 
                                flyingOutApp = null
                                swipeSignal = SwipeDirection.NONE
                                scope.launch {
                                    if (isRightSwipeUninstall) viewModel.keepApp(app.packageName) else viewModel.queueUninstall(app.packageName)
                                }
                            },
                            onSwipeRight = {
                                flyingOutApp = null
                                swipeSignal = SwipeDirection.NONE
                                scope.launch {
                                    if (isRightSwipeUninstall) viewModel.queueUninstall(app.packageName) else viewModel.keepApp(app.packageName)
                                }
                            },
                            onCategoryChanged = { newCat ->
                                viewModel.updateCategory(app.packageName, newCat)
                            },
                            isRightSwipeUninstall = isRightSwipeUninstall
                        )
                    }
                }
            } else {
                // Completed empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "All apps sorted",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Perfectly Decluttered!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You've sorted all installed apps on your device.\nTap below to re-scan or inspect your cleanup queue.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetAll() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Swipe Status")
                        }
                        Button(
                            onClick = { viewModel.syncApps() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-scan Apps")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactivity Button Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val leftBtnBg = if (isRightSwipeUninstall) MaterialTheme.colorScheme.primaryContainer else Color(0xFFFFE8F3)
                val leftBtnIconColor = if (isRightSwipeUninstall) MaterialTheme.colorScheme.primary else Color(0xFFFF0A7A)
                val leftBtnIcon = if (isRightSwipeUninstall) Icons.Default.Favorite else Icons.Default.Delete
                val leftBtnDesc = if (isRightSwipeUninstall) "Keep App" else "Queue Uninstall"

                val rightBtnBg = if (isRightSwipeUninstall) Color(0xFFFFE8F3) else MaterialTheme.colorScheme.primaryContainer
                val rightBtnIconColor = if (isRightSwipeUninstall) Color(0xFFFF0A7A) else MaterialTheme.colorScheme.primary
                val rightBtnIcon = if (isRightSwipeUninstall) Icons.Default.Delete else Icons.Default.Favorite
                val rightBtnDesc = if (isRightSwipeUninstall) "Queue Uninstall" else "Keep App"

                // Left Swipe Action Button
                LargeIconButton(
                    onClick = {
                        if (pendingList.isNotEmpty() && swipeSignal == SwipeDirection.NONE) {
                            swipeSignal = SwipeDirection.LEFT
                        }
                    },
                    backgroundColor = leftBtnBg,
                    iconColor = leftBtnIconColor,
                    icon = leftBtnIcon,
                    contentDescription = leftBtnDesc,
                    enabled = pendingList.isNotEmpty() && swipeSignal == SwipeDirection.NONE,
                    tag = "btn_left_action"
                )

                // Undo Button (🔄 Reconsider last action)
                OutlinedIconButton(
                    onClick = {
                        if (swipeSignal == SwipeDirection.NONE) {
                            viewModel.undoLastSwipe()
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("btn_undo"),
                    enabled = swipeSignal == SwipeDirection.NONE,
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reconsider Last Swipe", modifier = Modifier.size(24.dp))
                }

                // Right Swipe Action Button
                LargeIconButton(
                    onClick = {
                        if (pendingList.isNotEmpty() && swipeSignal == SwipeDirection.NONE) {
                            swipeSignal = SwipeDirection.RIGHT
                        }
                    },
                    backgroundColor = rightBtnBg,
                    iconColor = rightBtnIconColor,
                    icon = rightBtnIcon,
                    contentDescription = rightBtnDesc,
                    enabled = pendingList.isNotEmpty() && swipeSignal == SwipeDirection.NONE,
                    tag = "btn_right_action"
                )
            }
        }
    }
}

@Composable
fun LargeIconButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    iconColor: Color,
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    tag: String
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(64.dp)
            .shadow(4.dp, CircleShape)
            .testTag(tag),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = backgroundColor,
            contentColor = iconColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun SwipeCard(
    app: SwipeRecord,
    enabled: Boolean,
    scale: Float,
    offsetYOffset: androidx.compose.ui.unit.Dp,
    swipeSignal: SwipeDirection,
    onSwipeStart: () -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onCategoryChanged: (String) -> Unit,
    isRightSwipeUninstall: Boolean
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    var dragX by remember(app.packageName) { mutableStateOf(0f) }
    var dragY by remember(app.packageName) { mutableStateOf(0f) }
      val smoothSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val flyoutSpec = androidx.compose.animation.core.tween<Float>(
        durationMillis = 250,
        easing = androidx.compose.animation.core.FastOutLinearInEasing
    )
    
    // Swipe thresholds
    val swipeThreshold = with(density) { 140.dp.toPx() }
    val velocityTracker = remember { VelocityTracker() }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    var hasHapticFired by remember(app.packageName) { mutableStateOf(false) }

    // Intercept swipeSignal changes to trigger button swiping smoothly
    LaunchedEffect(swipeSignal) {
        if (enabled) {
            if (swipeSignal == SwipeDirection.LEFT) {
                onSwipeStart()
                androidx.compose.animation.core.animate(
                    initialValue = dragX,
                    targetValue = -3000f,
                    animationSpec = flyoutSpec
                ) { value, _ ->
                    dragX = value
                }
                onSwipeLeft()
            } else if (swipeSignal == SwipeDirection.RIGHT) {
                onSwipeStart()
                androidx.compose.animation.core.animate(
                    initialValue = dragX,
                    targetValue = 3000f,
                    animationSpec = flyoutSpec
                ) { value, _ ->
                    dragX = value
                }
                onSwipeRight()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .graphicsLayer {
                translationX = dragX
                translationY = dragY + offsetYOffset.toPx() * (1f - scale)
                scaleX = scale
                scaleY = scale
                rotationZ = (dragX / 15f).coerceIn(-15f, 15f)
            }
            .pointerInput(enabled, app.packageName) {
                if (!enabled) return@pointerInput
                detectDragGestures(
                    onDragStart = { velocityTracker.resetTracking() },
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val velocityY = velocityTracker.calculateVelocity().y
                        val targetX = dragX + (velocity / 3f) // projected position
                        
                        scope.launch {
                            val swipeLeft = targetX < -swipeThreshold
                            val swipeRight = targetX > swipeThreshold

                            if (swipeLeft) {
                                onSwipeStart()
                                androidx.compose.animation.core.animate(
                                    initialValue = dragX,
                                    targetValue = -3000f,
                                    initialVelocity = velocity,
                                    animationSpec = flyoutSpec
                                ) { value, _ ->
                                    dragX = value
                                }
                                onSwipeLeft()
                            } else if (swipeRight) {
                                onSwipeStart()
                                androidx.compose.animation.core.animate(
                                    initialValue = dragX,
                                    targetValue = 3000f,
                                    initialVelocity = velocity,
                                    animationSpec = flyoutSpec
                                ) { value, _ ->
                                    dragX = value
                                }
                                onSwipeRight()
                            } else {
                                launch {
                                    androidx.compose.animation.core.animate(
                                        initialValue = dragX,
                                        targetValue = 0f,
                                        initialVelocity = velocity,
                                        animationSpec = smoothSpring
                                    ) { value, _ ->
                                        dragX = value
                                    }
                                }
                                launch {
                                    androidx.compose.animation.core.animate(
                                        initialValue = dragY,
                                        targetValue = 0f,
                                        initialVelocity = velocityY,
                                        animationSpec = smoothSpring
                                    ) { value, _ ->
                                        dragY = value
                                    }
                                }
                            }
                        }
                    },
                    onDragCancel = {
                        val velocity = velocityTracker.calculateVelocity().x
                        val velocityY = velocityTracker.calculateVelocity().y
                        scope.launch {
                            launch {
                                androidx.compose.animation.core.animate(
                                    initialValue = dragX,
                                    targetValue = 0f,
                                    initialVelocity = velocity,
                                    animationSpec = smoothSpring
                                ) { value, _ ->
                                    dragX = value
                                }
                            }
                            launch {
                                androidx.compose.animation.core.animate(
                                    initialValue = dragY,
                                    targetValue = 0f,
                                    initialVelocity = velocityY,
                                    animationSpec = smoothSpring
                                ) { value, _ ->
                                    dragY = value
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        
                        if (abs(dragX) > swipeThreshold && !hasHapticFired) {
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            hasHapticFired = true
                        } else if (abs(dragX) < swipeThreshold && hasHapticFired) {
                            hasHapticFired = false
                        }
                    }
                )
            }
            .shadow(if (enabled) 12.dp else 4.dp, RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(
                1.5.dp, 
                if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), 
                RoundedCornerShape(24.dp)
            )
            .testTag("swipe_card_${app.packageName}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App visual logo avatar & category banner
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val catTheme = getCategoryTheme(app.category)
                    
                    // App Category Chip
                    Surface(
                        color = catTheme.color.copy(alpha = 0.15f),
                        contentColor = catTheme.color,
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(catTheme.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                  text = app.category,
                                  style = MaterialTheme.typography.labelSmall,
                                  fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // System App Badge
                    if (app.isSystemApp) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "SYSTEM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // App Hero Representation Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(getCategoryTheme(app.category).color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Elevated Icon Avatar
                    AppIconView(
                        packageName = app.packageName,
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // App Description
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Central stats section
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Storage Stat Block
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "STORAGE",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatSize(app.storageSizeMb),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Usage Stat Block
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "LAST TIME USED",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        val badgeColor = if (app.lastUsedDaysAgo >= 90) MaterialTheme.colorScheme.error else if (app.lastUsedDaysAgo >= 30) MaterialTheme.colorScheme.primary else Color(0xFF4CAF50)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(badgeColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (app.lastUsedDaysAgo) {
                                    0 -> "Today"
                                    1 -> "Yesterday"
                                    else -> "${app.lastUsedDaysAgo} days ago"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (app.lastUsedDaysAgo >= 90) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom category modifier inside Swipe view to re-categorize!
                var expandedCat by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expandedCat = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Category: ${app.category}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                    }
                    
                    DropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        listOf("Social", "Games", "Productivity", "Entertainment", "Utilities", "Other").forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    onCategoryChanged(cat)
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Dynamic Swipe Actions Based on Preference
        val leftSwipeText = if (isRightSwipeUninstall) "KEEP ⚡" else "SWIPE AWAY 🗑️"
        val leftSwipeColor = if (isRightSwipeUninstall) Color(0xFF1B5CFF) else Color(0xFFFF0A7A)
        
        val rightSwipeText = if (isRightSwipeUninstall) "SWIPE AWAY 🗑️" else "KEEP ⚡"
        val rightSwipeColor = if (isRightSwipeUninstall) Color(0xFFFF0A7A) else Color(0xFF1B5CFF)

        // Keep / Swipe Away Overlay (Left Swipe)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = if (dragX < 0f) (abs(dragX) / 200f).coerceIn(0f, 0.95f) else 0f
                }
                .background(leftSwipeColor.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
                .border(3.dp, leftSwipeColor, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = leftSwipeColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = leftSwipeText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }

        // Swipe Away / Keep Overlay (Right Swipe)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = if (dragX > 0f) (abs(dragX) / 200f).coerceIn(0f, 0.95f) else 0f
                }
                .background(rightSwipeColor.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
                .border(3.dp, rightSwipeColor, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = rightSwipeColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = rightSwipeText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
fun QueueScreen(viewModel: SwipeViewModel) {
    val queuedList by viewModel.queuedApps.collectAsStateWithLifecycle()
    val stats by viewModel.storageStatistics.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectToRemove by remember { mutableStateOf<SwipeRecord?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Header styled in Soft Cyan as a Review Screen highlight
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8FCFF)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF18D9E6).copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFF0A7A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Uninstall Batch Queue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF07112F)
                    )
                    Text(
                        text = "Recoverable Storage: ${formatSize(stats.queuedStorageMb)} from ${stats.queuedCount} apps",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF07112F).copy(alpha = 0.8f)
                    )
                }
            }
        }

        // List item rendering
        if (queuedList.isNotEmpty()) {
            Text(
                "BATCH REVIEW ITEMS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(queuedList, key = { it.packageName }) { app ->
                    QueueItemRow(
                        app = app,
                        onUninstallNative = {
                            viewModel.handoffUninstall(context, app)
                        },
                        onRestore = {
                            viewModel.removeFromQueue(app.packageName)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Batch Sequential Handoff Instructions
            Button(
                onClick = {
                    viewModel.uninstallMany(context, queuedList)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_trigger_uninstall_batch"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0A7A))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Sequential Uninstall", fontWeight = FontWeight.Bold)
            }
        } else {
            // Queue empty state
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "No items",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Decluttering Queue Empty!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Excellent! Swipe more apps from the core Swipe game screen to stack uninstall queue.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun QueueItemRow(
    app: SwipeRecord,
    onUninstallNative: () -> Unit,
    onRestore: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("queued_row_${app.packageName}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular app logo representation
                AppIconView(
                    packageName = app.packageName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Detail labels
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = getCategoryTheme(app.category).color,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.outlineVariant, CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatSize(app.storageSizeMb),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Reconsider RESTORE button
                OutlinedButton(
                    onClick = onRestore,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("btn_restore_${app.packageName}")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restore", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Keep", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(10.dp))

            // Swipe handoff action items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onUninstallNative,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("btn_uninstall_handoff_${app.packageName}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Uninstall", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun InsightsScreen(viewModel: SwipeViewModel) {
    val stats by viewModel.storageStatistics.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()
    val uninstalledList by viewModel.uninstalledApps.collectAsStateWithLifecycle()
    
    var activeCategoryFilter by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Decluttering Progress Card
        Text(
            text = "Declutter Analysis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "STORAGE OVERVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                // Huge highlight numbers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = formatSize(stats.totalStorageMb),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text("Total App Storage analyzed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatSize(stats.queuedStorageMb + stats.uninstalledStorageMb),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text("Total Space Freeable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar Visualizer
                val queuedFraction = if (stats.totalStorageMb > 0) (stats.queuedStorageMb.toFloat() + stats.uninstalledStorageMb.toFloat()) / stats.totalStorageMb.toFloat() else 0f
                val keptFraction = if (stats.totalStorageMb > 0) stats.savedStorageMb.toFloat() / stats.totalStorageMb.toFloat() else 0f
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sort Progress", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        val sortedCount = stats.totalApps - stats.pendingCount
                        val sortedPercent = if (stats.totalApps > 0) (sortedCount * 100) / stats.totalApps else 0
                        Text("$sortedPercent% Sorted ($sortedCount / ${stats.totalApps} apps)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Simple custom colorful progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            if (keptFraction > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(configWeight(keptFraction))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            if (queuedFraction > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(configWeight(queuedFraction))
                                        .background(MaterialTheme.colorScheme.error)
                                )
                            }
                            val remainingFraction = 1f - (keptFraction + queuedFraction)
                            if (remainingFraction > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(configWeight(remainingFraction))
                                        .background(Color.LightGray.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem(color = MaterialTheme.colorScheme.primary, text = "Kept (${formatSize(stats.savedStorageMb)})")
                    LegendItem(color = MaterialTheme.colorScheme.error, text = "Uninstall (${formatSize(stats.queuedStorageMb + stats.uninstalledStorageMb)})")
                    LegendItem(color = Color.LightGray, text = "Pending (${formatSize(stats.totalStorageMb - stats.savedStorageMb - stats.queuedStorageMb - stats.uninstalledStorageMb)})")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Category Breakdown Layout
        Text(
            text = "Category Analysis & Filtering",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val categoriesList = listOf("Social", "Games", "Productivity", "Entertainment", "Utilities", "Other")
        categoriesList.forEach { categoryName ->
            val appsInCategory = allRecords.filter { it.category == categoryName }
            val totalSize = appsInCategory.sumOf { it.storageSizeMb }
            val isSelectedFilter = activeCategoryFilter == categoryName
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable {
                        activeCategoryFilter = if (isSelectedFilter) null else categoryName
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelectedFilter) getCategoryTheme(categoryName).color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (isSelectedFilter) getCategoryTheme(categoryName).color else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(getCategoryTheme(categoryName).color, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(categoryName, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "${appsInCategory.size} Apps (${formatSize(totalSize)})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    
                    // Filter details when expanded
                    AnimatedVisibility(visible = isSelectedFilter) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            if (appsInCategory.isNotEmpty()) {
                                appsInCategory.forEach { record ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(record.appName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "Status: ${record.swipeStatus}",
                                                fontSize = 11.sp,
                                                color = if (record.swipeStatus == "KEPT") MaterialTheme.colorScheme.primary else if (record.swipeStatus == "QUEUED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(formatSize(record.storageSizeMb), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            } else {
                                Text("No apps categorized", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History logs
        Text(
            text = "Already Uninstalled Statistics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if (uninstalledList.isNotEmpty()) {
            val context = LocalContext.current
            val inlineInstallLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { }
            )
            uninstalledList.forEach { uninstalled ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uninstalled.appName, fontWeight = FontWeight.SemiBold)
                        Text("De-cluttered ${formatSize(uninstalled.storageSizeMb)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { 
                            val intent = viewModel.buildInlineInstallIntent(context, uninstalled.packageName)
                            if (intent != null) {
                                inlineInstallLauncher.launch(intent)
                            } else {
                                viewModel.launchFallbackStore(context, uninstalled.packageName)
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reinstall", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Text("No apps permanently uninstalled yet in this session.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SettingsScreen(viewModel: SwipeViewModel) {
    val isIgnoreSystem by viewModel.isIgnoreSystemApps.collectAsStateWithLifecycle()
    val isRightSwipeUninstall by viewModel.isRightSwipeUninstall.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SWIPE PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                        Text(
                            text = "Right Swipe to Uninstall",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "When active, swiping right queues apps for uninstall. When inactive, swiping left queues them.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isRightSwipeUninstall,
                        onCheckedChange = { viewModel.toggleRightSwipeUninstall() }
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FILTERS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                        Text(
                            text = "Ignore System Apps",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hide OS components, pre-installed dialers, and core system utilities from swipe & queue lists.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isIgnoreSystem,
                        onCheckedChange = { viewModel.toggleIgnoreSystemApps() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // System Control Panel
        OutlinedButton(
            onClick = { viewModel.resetAll() },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset Declutter Database", fontWeight = FontWeight.Bold)
        }
    }
}

fun configWeight(weight: Float): Float {
    return if (weight <= 0f) 0.0001f else weight
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
    }
}

fun formatSize(sizeMb: Int): String {
    return if (sizeMb >= 1000) {
        String.format("%.2f GB", sizeMb.toFloat() / 1000f)
    } else {
        "$sizeMb MB"
    }
}

// Visual layout mappings for Category colors and icons
data class CategoryTheme(val color: Color, val icon: ImageVector)

fun getCategoryTheme(category: String): CategoryTheme {
    return when (category) {
        "Social" -> CategoryTheme(Color(0xFF9C27B0), Icons.Default.Person) // Purple
        "Games" -> CategoryTheme(Color(0xFFE91E63), Icons.Default.Star) // Pink
        "Productivity" -> CategoryTheme(Color(0xFF009688), Icons.Default.Check) // Teal
        "Entertainment" -> CategoryTheme(Color(0xFF2196F3), Icons.Default.PlayArrow) // Blue
        "Utilities" -> CategoryTheme(Color(0xFFFF9800), Icons.Default.Settings) // Orange
        else -> CategoryTheme(Color(0xFF607D8B), Icons.Default.Info) // Grey
    }
}
