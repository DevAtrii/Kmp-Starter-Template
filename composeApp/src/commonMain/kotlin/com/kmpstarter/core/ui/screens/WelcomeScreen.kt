package com.kmpstarter.core.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kmpstarter.core.datastore.theme.ThemeDataStore
import com.kmpstarter.core.datastore.theme.isAppInDarkTheme
import com.kmpstarter.core.events.controllers.SnackbarController
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.ui.layouts.lists.ScrollableColumn
import com.kmpstarter.core.ui.utils.screen.ScreenSizeValue
import com.kmpstarter.core.utils.intents.IntentUtils
import com.kmpstarter.core.utils.platform.isDynamicColorSupported
import com.kmpstarter.theme.Dimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit,
    intentUtils: IntentUtils = koinInject(),
    themeDataStore: ThemeDataStore = koinInject(),
) {
    var showContent by remember { mutableStateOf(false) }
    var showBlur by remember { mutableStateOf(false) }
    var clickCount by remember { mutableIntStateOf(0) }
    var buttonText by remember { mutableStateOf("Toggle") }

    // Responsive breakpoints
    val screenWidth = ScreenSizeValue.width
    val isCompact = screenWidth < 600.dp
    val isMedium = screenWidth in 600.dp..840.dp
    val isExpanded = screenWidth > 840.dp

    LaunchedEffect(Unit) {
        showBlur = true
        delay(300)
        showContent = true
    }

    // Snackbar examples
    /*LaunchedEffect(Unit) {
        delay(2000)
        SnackbarController.sendMessage("Welcome to KMP Starter! 🚀")
        delay(3000)
        SnackbarController.sendAlert(
            message = "Try the theme toggle to see dynamic colors!",
            actionName = "Got it"
        ) {
            SnackbarController.sendMessage("Great! You're all set!")
        }
    }*/

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background with gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
        )

        // Animated blur overlay
        AnimatedVisibility(
            visible = showBlur,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 800)
            ),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                    )
            )
        }

        // Main content with responsive layout
        if (isExpanded) {
            // Desktop/Tablet landscape layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.paddingLarge),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingLarge)
            ) {
                // Left side - Hero section
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) { it / 3 } + fadeIn(
                            animationSpec = tween(durationMillis = 1000)
                        ),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        HeroSection(isCompact = false)
                    }
                }

                // Right side - Features and actions
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) { it / 3 } + fadeIn(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 200)
                        ),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        FeaturesSection(isCompact = false)
                    }

                    Spacer(modifier = Modifier.height(Dimens.paddingExtraLarge))
                    val scope = rememberCoroutineScope()
                    AnimatedVisibility(
                        visible = showContent,
                        enter = slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) { it / 3 } + fadeIn(
                            animationSpec = tween(durationMillis = 1000, delayMillis = 400)
                        ),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        ActionButtonsSection(
                            isCompact = false,
                            onGetStartedClick = onGetStartedClick,
                            themeDataStore = themeDataStore,
                            intentUtils = intentUtils,
                            buttonText = buttonText,
                            onThemeClick = {
                                val themeMode = when (clickCount) {
                                    0 -> {
                                        clickCount++
                                        buttonText = "Light"
                                        ThemeMode.LIGHT
                                    }

                                    1 -> {
                                        clickCount++
                                        buttonText = "Dark"
                                        ThemeMode.DARK
                                    }

                                    else -> {
                                        clickCount = 0
                                        buttonText = "System"
                                        ThemeMode.SYSTEM
                                    }
                                }
                                themeDataStore.setThemeMode(themeMode = themeMode)
                                scope.launch {
                                    SnackbarController.sendAlert("Theme changed to ${themeMode.name.lowercase()}")

                                }
                            }
                        )
                    }
                }
            }
        } else {
            // Mobile/Tablet portrait layout
            ScrollableColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isCompact) Dimens.paddingMedium else Dimens.paddingLarge,
                        vertical = Dimens.paddingLarge
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.paddingLarge)
            ) {
                // Hero Section
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) { it / 3 } + fadeIn(
                        animationSpec = tween(durationMillis = 1000)
                    ),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    HeroSection(isCompact = isCompact)
                }

                // Features Section
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) { it / 3 } + fadeIn(
                        animationSpec = tween(durationMillis = 1000, delayMillis = 200)
                    ),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    FeaturesSection(isCompact = isCompact)
                }

                // Action Buttons
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) { it / 3 } + fadeIn(
                        animationSpec = tween(durationMillis = 1000, delayMillis = 400)
                    ),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    val scope = rememberCoroutineScope()
                    ActionButtonsSection(
                        isCompact = isCompact,
                        themeDataStore = themeDataStore,
                        intentUtils = intentUtils,
                        onGetStartedClick = onGetStartedClick,
                        buttonText = buttonText,
                        onThemeClick = {
                            val themeMode = when (clickCount) {
                                0 -> {
                                    clickCount++
                                    buttonText = "Light"
                                    ThemeMode.LIGHT
                                }

                                1 -> {
                                    clickCount++
                                    buttonText = "Dark"
                                    ThemeMode.DARK
                                }

                                else -> {
                                    clickCount = 0
                                    buttonText = "System"
                                    ThemeMode.SYSTEM
                                }
                            }
                            themeDataStore.setThemeMode(themeMode = themeMode)
                            scope.launch {
                                SnackbarController.sendAlert("Theme changed to ${themeMode.name.lowercase()}")

                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection(isCompact: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated logo with blur effect
        val animatedScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "logo_scale"
        )

        Surface(
            modifier = Modifier
                .size(if (isCompact) 80.dp else 100.dp)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                },
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
            shadowElevation = Dimens.elevationLarge
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Rocket,
                    contentDescription = "KMP Starter",
                    modifier = Modifier.size(if (isCompact) 40.dp else 48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        // Title with gradient text effect
        Text(
            text = "KMP Starter",
            style = if (isCompact) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.paddingSmall))

        // Subtitle with subtle animation
        Text(
            text = "Build once, deploy everywhere",
            style = if (isCompact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        // Description with improved typography
        Text(
            text = "A modern, production-ready Kotlin Multiplatform template with Material 3 design and comprehensive tooling.",
            style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.paddingMedium),
            lineHeight = if (isCompact) 20.sp else 24.sp
        )
    }
}

@Composable
private fun FeaturesSection(isCompact: Boolean) {
    Column {
        Text(
            text = "Why KMP Starter?",
            style = if (isCompact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        val features = listOf(
            FeatureItem(
                icon = Icons.Default.Code,
                title = "Cross-Platform",
                description = "Write once, run everywhere"
            ),
            FeatureItem(
                icon = Icons.Default.Palette,
                title = "Material 3",
                description = "Modern design system"
            ),
            FeatureItem(
                icon = Icons.Default.Storage,
                title = "Room Database",
                description = "Local data persistence"
            ),
            FeatureItem(
                icon = Icons.Default.Tune,
                title = "Dependency Injection",
                description = "Clean architecture"
            ),
            FeatureItem(
                icon = Icons.Default.Bolt,
                title = "Coroutines & Flow",
                description = "Reactive programming"
            ),
            FeatureItem(
                icon = Icons.Default.Star,
                title = "RevenueCat",
                description = "In-app purchases"
            )
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isCompact) 2 else 3),
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium),
            modifier = Modifier.height(if (isCompact) 280.dp else 320.dp)
        ) {
            items(features) { feature ->
                ModernFeatureCard(
                    feature = feature,
                    isCompact = isCompact
                )
            }
        }
    }
}

@Composable
private fun ModernFeatureCard(
    feature: FeatureItem,
    isCompact: Boolean,
) {
    var isHovered by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(
        targetValue = if (isHovered) Dimens.elevationLarge else Dimens.elevationSmall,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isHovered) 1.02f else 1f
                scaleY = if (isHovered) 1.02f else 1f
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isHovered = true
                        tryAwaitRelease()
                        isHovered = false
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isCompact) Dimens.paddingSmall else Dimens.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(if (isCompact) 32.dp else 40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = feature.title,
                        modifier = Modifier.size(if (isCompact) 16.dp else 20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))

            Text(
                text = feature.title,
                style = if (isCompact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = feature.description,
                style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    isCompact: Boolean,
    themeDataStore: ThemeDataStore,
    intentUtils: IntentUtils,
    buttonText: String,
    onThemeClick: () -> Unit,
    onGetStartedClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Dynamic color toggle (Android only) with custom animated toggle
        if (isDynamicColorSupported) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                DynamicColorToggle(
                    themeDataStore = themeDataStore,
                    isCompact = isCompact
                )
            }

            Spacer(modifier = Modifier.height(Dimens.paddingMedium))
        }

        // Secondary actions in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
        ) {
            // Theme Toggle
            OutlinedButton(
                onClick = onThemeClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = if (!isAppInDarkTheme())
                        Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle theme",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // Documentation
            val scope = rememberCoroutineScope()
            OutlinedButton(
                onClick = {
                    intentUtils.openUrl(
                        url = "https://github.com/DevAtrii/Kmp-Starter-Template"
                    )
                    scope.launch {
                        SnackbarController.sendAlert("Opening documentation...")
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
                Text(
                    text = "Docs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        // Primary action button
        val scope = rememberCoroutineScope()
        Button(
            onClick = {
                scope.launch {
                    SnackbarController.sendAlert(
                        message = "Navigate to dummy purchase screen?",
                        actionName = "Let's go!"
                    ) {
                        SnackbarController.sendMessage("🚀 Starting your KMP journey!")
                        onGetStartedClick()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Dimens.paddingSmall))
            Text(
                text = "Get Started",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DynamicColorToggle(
    themeDataStore: ThemeDataStore,
    isCompact: Boolean,
) {
    val scope = rememberCoroutineScope()
    var isDynamicColorEnabled by remember { mutableStateOf(false) }
    var isAnimating by remember { mutableStateOf(false) }

    // Animated values
    val rocketScale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rocket_scale"
    )

    val rocketRotation by animateFloatAsState(
        targetValue = if (isAnimating) 360f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = androidx.compose.animation.core.EaseInOutCubic
        ),
        label = "rocket_rotation"
    )

    val toggleOffset by animateDpAsState(
        targetValue = if (isDynamicColorEnabled) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "toggle_offset"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isDynamicColorEnabled)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(durationMillis = 300),
        label = "toggle_background"
    )

    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.05f else 1.0f,
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimens.elevationMedium
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) Dimens.paddingMedium else Dimens.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Icon and text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
            ) {
                // Animated rocket icon
                Box(
                    modifier = Modifier
                        .size(if (isCompact) 32.dp else 40.dp)
                        .graphicsLayer {
                            scaleX = rocketScale
                            scaleY = rocketScale
                            rotationZ = rocketRotation
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(if (isCompact) 28.dp else 36.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Rocket,
                                contentDescription = "Dynamic Colors",
                                modifier = Modifier.size(if (isCompact) 16.dp else 20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "Dynamic Colors",
                        style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isDynamicColorEnabled) "Enabled" else "Disabled",
                        style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right side - Custom toggle
            Box(
                modifier = Modifier
                    .size(
                        width = if (isCompact) 48.dp else 56.dp,
                        height = if (isCompact) 24.dp else 28.dp
                    )
                    .background(
                        color = if (isDynamicColorEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures {
                            isAnimating = true
                            isDynamicColorEnabled = !isDynamicColorEnabled

                            scope.launch {
                                themeDataStore.setOppositeDynamicColor()
                                SnackbarController.sendAlert(
                                    if (isDynamicColorEnabled) "Dynamic colors enabled! 🎨"
                                    else "Dynamic colors disabled"
                                )

                                delay(800) // Wait for animation to complete
                                isAnimating = false
                            }
                        }
                    },
                contentAlignment = Alignment.CenterStart
            ) {

                Box(
                    modifier = Modifier
                        .offset(x = toggleOffset)
                        .size(if (isCompact) 20.dp else 24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(50)
                        )
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Rocket trail effect when animating
                    if (isAnimating) {
                        this@Row.AnimatedVisibility(
                            visible = isAnimating,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Row(
                                modifier = Modifier.offset(x = (-8).dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (isCompact) 3.dp else 4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f - index * 0.2f),
                                                shape = RoundedCornerShape(50)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

