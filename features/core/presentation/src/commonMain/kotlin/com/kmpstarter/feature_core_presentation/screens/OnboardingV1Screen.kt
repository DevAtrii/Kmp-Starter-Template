package com.kmpstarter.feature_core_presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmpstarter.feature_core_presentation.viewmodels.OnboardingActions
import com.kmpstarter.feature_core_presentation.viewmodels.OnboardingEvents
import com.kmpstarter.feature_core_presentation.viewmodels.OnboardingViewModel
import com.kmpstarter.feature_resources.Res
import com.kmpstarter.feature_resources.compose_multiplatform
import com.kmpstarter.feature_resources.starter_onboarding_continue
import com.kmpstarter.feature_resources.starter_onboarding_slide_1_description
import com.kmpstarter.feature_resources.starter_onboarding_slide_1_title
import com.kmpstarter.feature_resources.starter_onboarding_slide_2_description
import com.kmpstarter.feature_resources.starter_onboarding_slide_2_title
import com.kmpstarter.feature_resources.starter_onboarding_slide_3_description
import com.kmpstarter.feature_resources.starter_onboarding_slide_3_title
import com.kmpstarter.feature_resources.starter_onboarding_source_friend
import com.kmpstarter.feature_resources.starter_onboarding_source_google_play
import com.kmpstarter.feature_resources.starter_onboarding_source_other
import com.kmpstarter.feature_resources.starter_onboarding_source_reddit
import com.kmpstarter.feature_resources.starter_onboarding_source_social_media
import com.kmpstarter.feature_resources.starter_onboarding_source_whatsapp
import com.kmpstarter.feature_resources.starter_onboarding_source_youtube
import com.kmpstarter.feature_resources.toActualString
import com.kmpstarter.ui_components.animations.FadeIn
import com.kmpstarter.ui_components.animations.FadeInTokens
import com.kmpstarter.ui_components.animations.Floating
import com.kmpstarter.ui_components.lists.ScrollableColumn
import com.kmpstarter.ui_components.progress.StepsProgress
import com.kmpstarter.ui_utils.common_composables.VerticalSpacer
import com.kmpstarter.ui_utils.side_effects.ObserveAsEvents
import com.kmpstarter.ui_utils.theme.Dimens
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel


private data class TrafficSource(
    val id: String,
    val title: StringResource,
    val icon: ImageVector,
)

private val trafficSources = listOf(
    TrafficSource(
        id = "google_play",
        title = Res.string.starter_onboarding_source_google_play,
        icon = Icons.Default.PlayArrow
    ),
    TrafficSource(
        id = "social_media",
        title = Res.string.starter_onboarding_source_social_media,
        icon = Icons.Default.ThumbUp
    ),
    TrafficSource(
        id = "reddit",
        title = Res.string.starter_onboarding_source_reddit,
        icon = Icons.AutoMirrored.Filled.Chat
    ),
    TrafficSource(
        id = "youtube",
        title = Res.string.starter_onboarding_source_youtube,
        icon = Icons.Default.PlayArrow
    ),
    TrafficSource(
        id = "whatsapp",
        title = Res.string.starter_onboarding_source_whatsapp,
        icon = Icons.Default.Whatsapp
    ),
    TrafficSource(
        id = "friend",
        title = Res.string.starter_onboarding_source_friend,
        icon = Icons.Default.Person
    ),
    TrafficSource(
        id = "other",
        title = Res.string.starter_onboarding_source_other,
        icon = Icons.Default.MoreHoriz
    )
)


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OnboardingV1Screen(
    onNavigate: () -> Unit,
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(state.currentSlide != 1) {
        viewModel.onAction(action = OnboardingActions.OnStepDecrement)
    }

    ObserveAsEvents(
        flow = viewModel.uiEvents
    ) { event ->
        when (event) {
            OnboardingEvents.Finish -> onNavigate()
        }
    }


    OnboardingV1ScreenContent(
        currentSlide = state.currentSlide,
        totalSlides = state.totalSlides,
        selectedTrafficSource = state.selectedTrafficSource,
        onTrafficSourceChange = { value ->
            viewModel.onAction(action = OnboardingActions.OnTrafficSourceChange(value = value))
        },
        onContinueClick = {
            viewModel.onAction(action = OnboardingActions.OnStepIncrement)
        },
        onFinish = {
            viewModel.onAction(action = OnboardingActions.OnFinish)
        },
    )
}

@Composable
private fun OnboardingV1ScreenContent(
    currentSlide: Int,
    totalSlides: Int,
    selectedTrafficSource: String?,
    onTrafficSourceChange: (sourceId: String) -> Unit,
    onContinueClick: () -> Unit,
    onFinish: () -> Unit,
) {
    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .padding(Dimens.paddingSmall).fillMaxSize()
        ) {
            StepsProgress(
                steps = totalSlides,
                currentStep = currentSlide,
            )
            AnimatedContent(
                modifier = Modifier
                    .weight(1f),
                targetState = currentSlide,
                transitionSpec = {
                    fadeIn() + slideInHorizontally(
                        initialOffsetX = { if (targetState > initialState) it else -it },
                    ) togetherWith fadeOut() + slideOutHorizontally(
                        targetOffsetX = { if (targetState > initialState) -it else it },
                    )
                }
            ) { currentSlide ->

                when (currentSlide) {

                    1 -> Slide(
                        image = Res.drawable.compose_multiplatform,
                        title = Res.string.starter_onboarding_slide_1_title,
                        description = Res.string.starter_onboarding_slide_1_description,
                        onContinueClick = onContinueClick,
                    )

                    2 -> Slide(
                        image = Res.drawable.compose_multiplatform,
                        title = Res.string.starter_onboarding_slide_2_title,
                        description = Res.string.starter_onboarding_slide_2_description,
                        onContinueClick = onContinueClick,
                    )

                    3 -> TrafficSourceSlide(
                        modifier = Modifier,
                        title = Res.string.starter_onboarding_slide_3_title,
                        description = Res.string.starter_onboarding_slide_3_description,
                        selectedSource = selectedTrafficSource,
                        onSourceChange = onTrafficSourceChange,
                        onContinueClick = onFinish
                    )
                }

            }
        }
    }

}

@Composable
private fun TrafficSourceSlide(
    modifier: Modifier = Modifier,
    title: StringResource,
    description: StringResource,
    selectedSource: String?,
    onSourceChange: (sourceId: String) -> Unit,
    onContinueClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.paddingMedium)
    ) {
        ScrollableColumn(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            VerticalSpacer(height = 60.dp)

            FadeIn(
                delayMillis = FadeInTokens.DELAY_1
            ) {
                OnBoardingTextTitle(
                    modifier = Modifier.padding(horizontal = Dimens.paddingSmall),
                    stringResource = title
                )
            }

            VerticalSpacer(Dimens.paddingMedium)

            FadeIn(
                delayMillis = FadeInTokens.DELAY_2
            ) {
                OnBoardingTextBody(
                    modifier = Modifier.padding(horizontal = Dimens.paddingSmall),
                    stringResource = description
                )
            }
            VerticalSpacer(Dimens.paddingLarge)
            FadeIn(
                delayMillis = FadeInTokens.DELAY_3
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                ) {
                    trafficSources.forEach { source ->
                        TrafficSourceOptionCard(
                            modifier = Modifier,
                            title = source.title.toActualString(),
                            icon = source.icon,
                            isSelected = source.id == selectedSource,
                            onClick = {
                                onSourceChange(source.id)
                            }
                        )
                    }
                }
            }
        }

        FadeIn(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.paddingMedium),
            delayMillis = FadeInTokens.DELAY_3,
        ) {
            OnBoardingContinueButton(
                onContinueClick = onContinueClick,
                enabled = selectedSource != null
            )
        }
    }

}


@Composable
private fun TrafficSourceOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) Dimens.elevationMedium else Dimens.elevationSmall
        ),
        shape = RoundedCornerShape(Dimens.paddingMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun Slide(
    modifier: Modifier = Modifier,
    image: DrawableResource,
    title: StringResource,
    description: StringResource,
    onContinueClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.paddingMedium)
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            VerticalSpacer(height = 60.dp)

            FadeIn(
                delayMillis = FadeInTokens.DELAY_1
            ) {
                Floating {
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1.2f),
                        painter = painterResource(image),
                        contentDescription = null
                    )
                }
            }

            VerticalSpacer(height = 40.dp)

            FadeIn(
                delayMillis = FadeInTokens.DELAY_2
            ) {
                OnBoardingTextTitle(
                    modifier = Modifier.padding(horizontal = Dimens.paddingSmall),
                    stringResource = title
                )
            }

            VerticalSpacer(Dimens.paddingMedium)

            FadeIn(
                delayMillis = FadeInTokens.DELAY_3
            ) {
                OnBoardingTextBody(
                    modifier = Modifier.padding(horizontal = Dimens.paddingSmall),
                    stringResource = description
                )
            }
        }

        FadeIn(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.paddingMedium),
            delayMillis = FadeInTokens.DELAY_4
        ) {
            OnBoardingContinueButton(
                onContinueClick = onContinueClick
            )
        }
    }

}

@Composable
private fun OnBoardingTextTitle(
    modifier: Modifier = Modifier,
    stringResource: StringResource,
) {
    Text(
        modifier = modifier,
        text = stringResource.toActualString(),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun OnBoardingTextBody(
    modifier: Modifier = Modifier,
    stringResource: StringResource,
) {
    Text(
        modifier = modifier,
        text = stringResource.toActualString(),
        style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
        ),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun BoxScope.OnBoardingContinueButton(
    modifier: Modifier = Modifier,
    text: String = Res.string.starter_onboarding_continue.toActualString(),
    enabled: Boolean = true,
    onContinueClick: () -> Unit,
) {
    Button(
        onClick = onContinueClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .align(Alignment.BottomCenter)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}