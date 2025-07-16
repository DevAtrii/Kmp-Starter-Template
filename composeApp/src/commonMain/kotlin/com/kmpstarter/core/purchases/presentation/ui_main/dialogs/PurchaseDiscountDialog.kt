package com.kmpstarter.core.purchases.presentation.ui_main.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kmpstarter.core.purchases.presentation.viewmodels.PurchaseDialogViewModel
import com.kmpstarter.core.ui.components.buttons.LoadingButton
import com.kmpstarter.core.utils.common.epochMillis
import com.kmpstarter.theme.Dimens
import com.revenuecat.purchases.kmp.models.PeriodUnit
import com.revenuecat.purchases.kmp.models.StoreProduct
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PurchaseDiscountDialog(
    modifier: Modifier = Modifier,
    viewModel: PurchaseDialogViewModel = koinViewModel(),
    isLoading: Boolean,
    discountProduct: StoreProduct,
    discountPercentage: Double,
    onDismiss: () -> Unit = {},
    onAccept: () -> Unit = {},
) {
    val offerExpirationMillis by viewModel.expirationMillis.collectAsState()
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        PurchaseDiscountDialogContent(
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimens.paddingMedium),
            isLoading = isLoading,
            discountPrice = discountProduct.price.formatted,
            discountPercentage = "$discountPercentage%",
            expirationMillis = offerExpirationMillis,
            offerValidity = when (discountProduct.period?.unit) {
                PeriodUnit.DAY -> "Daily"
                PeriodUnit.WEEK -> "Weekly"
                PeriodUnit.MONTH -> "Monthly"
                PeriodUnit.YEAR -> "Yearly"
                else -> "Unknown"
            },
            onDismiss = {
                onDismiss()
            },
            onPurchaseClick = onAccept
        )
    }
}

@Composable
private fun PurchaseDiscountDialogContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    discountPrice: String,
    discountPercentage: String,
    expirationMillis: Long,
    offerValidity: String,
    onDismiss: () -> Unit,
    onPurchaseClick: () -> Unit,
) {
    var remainingMillis by remember { mutableStateOf(0L) }

    // Initialize timer
    LaunchedEffect(expirationMillis) {
        remainingMillis = expirationMillis - epochMillis()
    }

    // Format remaining time using Duration
    val duration = remainingMillis.milliseconds
    val hours = duration.inWholeHours
    val minutes = (duration.inWholeMinutes % 60)
    val seconds = (duration.inWholeSeconds % 60)

    // Update countdown timer
    LaunchedEffect(Unit) {
        while (remainingMillis > 0) {
            delay(1000)
            remainingMillis -= 1000
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add offer validity
            Text(
                text = offerValidity,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Discount percentage
            Text(
                text = discountPercentage,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "DISCOUNT",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Countdown timer
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeUnit(hours.toString().padStart(2, '0'))
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = Dimens.paddingExtraSmall)
                )
                TimeUnit(minutes.toString().padStart(2, '0'))
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = Dimens.paddingExtraSmall)
                )
                TimeUnit(seconds.toString().padStart(2, '0'))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price and description
            Text(
                text = "Limited Time Offer!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Unlock all premium features for just",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = discountPrice,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Button
            LoadingButton(
                text = "Get Premium Now",
                loadingText = "Processing...",
                onClick = onPurchaseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = true,
                isLoading = isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}

@Composable
private fun TimeUnit(
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(80.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = value,
                modifier = Modifier.padding(8.dp)
            ) { target ->
                Text(
                    text = target,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



















