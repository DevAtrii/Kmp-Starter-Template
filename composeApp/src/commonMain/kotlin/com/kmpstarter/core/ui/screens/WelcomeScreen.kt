package com.kmpstarter.core.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.kmpstarter.core.events.ThemeEvents
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.events.isAppInDarkTheme
import com.kmpstarter.core.utils.intents.IntentUtils
import com.kmpstarter.theme.Dimens
import kotlinx.coroutines.delay
import org.koin.compose.koinInject


@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    intentUtils: IntentUtils = koinInject(),
) {
    var showContent by remember { mutableStateOf(false) }
    var showBlur by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showBlur = true
        delay(300)
        showContent = true
    }

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

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                HeroSection()
            }

            Spacer(modifier = Modifier.height(Dimens.paddingExtraLarge))

            // Features Grid
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
                FeaturesGrid()
            }

            Spacer(modifier = Modifier.height(Dimens.paddingExtraLarge))

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
                ModernActionButtons(
                    onOpenDocsClick = {
                        intentUtils.openUrl(
                            url = "https://github.com/DevAtrii/Kmp-Starter-Template/tree/main"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun HeroSection() {
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
                .size(100.dp)
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
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        // Title with gradient text effect
        Text(
            text = "KMP Starter",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.paddingSmall))

        // Subtitle with subtle animation
        Text(
            text = "Build once, deploy everywhere",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        // Description with improved typography
        Text(
            text = "A modern, production-ready Kotlin Multiplatform template with Material 3 design and comprehensive tooling.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimens.paddingMedium),
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun FeaturesGrid() {
    Column {
        Text(
            text = "Why KMP Starter?",
            style = MaterialTheme.typography.headlineMedium,
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
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium),
            modifier = Modifier.height(320.dp)
        ) {
            items(features) { feature ->
                ModernFeatureCard(feature = feature)
            }
        }
    }
}

@Composable
private fun ModernFeatureCard(feature: FeatureItem) {
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
                .padding(Dimens.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = feature.title,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.paddingSmall))

            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ModernActionButtons(
    themeEvents: ThemeEvents = koinInject(),
    onOpenDocsClick: () -> Unit,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Secondary actions in a row
        var clickCount by remember {
            mutableIntStateOf(0)
        }
        var buttonText by remember {
            mutableStateOf("Toggle")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
        ) {
            // Theme Toggle
            OutlinedButton(
                onClick = {
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
                    themeEvents.setThemeMode(
                        themeMode = themeMode
                    )

                },
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
            OutlinedButton(
                onClick = onOpenDocsClick,
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
    }
}

private data class FeatureItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

