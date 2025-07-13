package com.kmpstarter.core.ui.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmpstarter.core.ui.components.icons.GoogleIcon
import com.kmpstarter.core.ui.utils.screen.getScreenSize
import com.kmpstarter.theme.Dimens

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GoogleSignInButtonUI(
    isGoogleSignInLoading: Boolean,
    onGoogleSignInClick: () -> Unit,
) {
    val screenWidth = getScreenSize().width.value
    val buttonWidth by animateFloatAsState(
        targetValue = if (isGoogleSignInLoading) 56f else screenWidth - 32f,
        animationSpec =
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "width_animation",
    )

    OutlinedButton(
        onClick = onGoogleSignInClick,
        enabled = !isGoogleSignInLoading,
        modifier =
        Modifier
            .width(buttonWidth.dp)
            .height(Dimens.buttonHeight),
        border =
        BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
        colors =
        ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        contentPadding =
        PaddingValues(
            horizontal = if (isGoogleSignInLoading) 0.dp else 16.dp,
            vertical = 0.dp,
        ),
    ) {
        AnimatedContent(
            targetState = isGoogleSignInLoading,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(220, delayMillis = 90),
                ) +
                        scaleIn(
                            animationSpec = tween(220, delayMillis = 90),
                        ) togetherWith fadeOut(
                    animationSpec = tween(90),
                ) +
                        scaleOut(
                            animationSpec = tween(90),
                        )
            },
            label = "loading_animation",
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    GoogleIcon(
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(Dimens.paddingMedium))
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}
