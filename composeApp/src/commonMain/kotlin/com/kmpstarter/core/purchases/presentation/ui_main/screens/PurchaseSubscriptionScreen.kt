package com.kmpstarter.core.purchases.presentation.ui_main.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmpstarter.core.purchases.presentation.events.PurchaseEvent
import com.kmpstarter.core.purchases.presentation.models.PurchaseFeature
import com.kmpstarter.core.purchases.presentation.ui_main.dialogs.PurchaseDiscountDialog
import com.kmpstarter.core.purchases.presentation.viewmodels.PurchaseDialogViewModel
import com.kmpstarter.core.purchases.presentation.viewmodels.PurchaseViewModel
import com.kmpstarter.core.ui.layouts.lists.ScrollableColumn
import com.kmpstarter.core.ui.layouts.loading.LoadingLayout
import com.kmpstarter.core.ui.utils.screen.ScreenSizeValue
import com.kmpstarter.core.utils.common.currentMillis
import com.kmpstarter.theme.Dimens
import com.revenuecat.purchases.kmp.models.StoreProduct
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PurchaseSubscriptionScreen(
    modifier: Modifier = Modifier,
    viewModel: PurchaseViewModel = koinViewModel(),
    features: List<PurchaseFeature>,
    subscriptionTerms: List<String> = listOf(),
    onDismiss: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    if (state.isLoading) {
        LoadingLayout()
        return
    }

    PurchaseSubscriptionScreenContent(
        modifier = modifier,
        selectedProduct = state.selectedProduct,
        features = features,
        products = state.products,
        subscriptionTerms = subscriptionTerms,
        discountProduct = state.discountProduct,
        discountPercentage = state.discountPercentage,
        isLoading = state.isLoading,
        onDismiss = onDismiss,
        onRestoreClick = {
            viewModel.onEvent(
                event = PurchaseEvent.RestorePurchase
            )
        },
        onPurchaseClick = {
            state.selectedProduct ?: return@PurchaseSubscriptionScreenContent
            viewModel.onEvent(
                event = PurchaseEvent.StartPurchase(
                    product = state.selectedProduct!!
                )
            )
        },
        onProductSelected = viewModel::onSelectedProductChange
    )

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PurchaseSubscriptionScreenContent(
    modifier: Modifier = Modifier,
    viewModel: PurchaseDialogViewModel = koinViewModel(),
    selectedProduct: StoreProduct?,
    features: List<PurchaseFeature>,
    products: List<StoreProduct>,
    subscriptionTerms: List<String> = listOf(),
    discountProduct: StoreProduct? = null,
    discountPercentage: Double? = null,
    cancelTimerTimeMillis: Long = 5000L,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onRestoreClick: () -> Unit,
    onPurchaseClick: () -> Unit,
    onProductSelected: (StoreProduct) -> Unit,
) {
    require(
        value = products.size <= 3,
    ) {
        "Maximum 3 products are allowed for this subscription screen but ${products.size} is supplied, " +
                "Fix it otherwise UI won't be responsive KmpStarter.core.purchases@PurchaseSubscriptionScreenContent"
    }

    var showDiscountDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val canShowDiscountDialog by viewModel.showDiscountDialog.collectAsState()

    var isDialogShown by rememberSaveable {
        mutableStateOf(false)
    }
    BackHandler(
        enabled = canShowDiscountDialog && discountProduct != null && !isDialogShown
    ) {
        showDiscountDialog = true
        isDialogShown = true
    }

    var cancelTimerProgress by rememberSaveable {
        mutableStateOf(1f)
    }

    var showCancelButton by rememberSaveable {
        mutableStateOf(false)
    }

    var isTimerActive by rememberSaveable {
        mutableStateOf(cancelTimerTimeMillis > 0)
    }

    // Responsive breakpoints
    val screenWidth = ScreenSizeValue.width
    val isCompact = screenWidth < 600.dp
    val isMedium = screenWidth in 600.dp..840.dp
    val isExpanded = screenWidth > 840.dp

    // Determine which product to highlight based on count
    val highlightedProductIndex = when (products.size) {
        1 -> 0
        2 -> 1 // Second product (index 1)
        3 -> 1 // Center product (index 1)
        else -> 0
    }

    // Cancel timer effect
    LaunchedEffect(isTimerActive) {
        if (isTimerActive && cancelTimerTimeMillis > 0) {
            val startTime = currentMillis
            val endTime = startTime + cancelTimerTimeMillis

            while (currentMillis < endTime) {
                val elapsed = currentMillis - startTime
                cancelTimerProgress = 1f - (elapsed.toFloat() / cancelTimerTimeMillis.toFloat())
                delay(16) // ~60fps
            }

            cancelTimerProgress = 0f
            showCancelButton = true
            isTimerActive = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
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
            // Header Section
            HeaderSection(isCompact = isCompact)

            // Products Section
            ProductsSection(
                products = products,
                selectedProduct = selectedProduct,
                highlightedProductIndex = highlightedProductIndex,
                isCompact = isCompact,
                onProductSelected = onProductSelected
            )

            // Features Section
            FeaturesSection(
                features = features,
                isCompact = isCompact
            )

            // Action Buttons


            // Terms Section
            if (subscriptionTerms.isNotEmpty()) {
                TermsSection(
                    terms = subscriptionTerms,
                    isCompact = isCompact
                )
            }
            Spacer(
                modifier = Modifier.padding(
                    100.dp
                )
            )
        }

        // Cancel Timer Progress Bar
        if (isTimerActive && cancelTimerTimeMillis > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)

            ) {
                CircularProgressIndicator(
                    progress = { cancelTimerProgress },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(Dimens.paddingMedium),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
            }
        }

        // Cancel Button (appears after timer)
        AnimatedVisibility(
            visible = showCancelButton,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimens.paddingMedium)
        ) {
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        if (!canShowDiscountDialog || discountProduct == null) {
                            onDismiss()
                            return@clickable
                        }
                        showDiscountDialog = true
                    },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shadowElevation = Dimens.elevationMedium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        // floating action buttons
        val shape = remember {
            RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
            )
        }
        ActionButtonsSection(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .border(
                    width = 1.dp,
                    shape = shape,
                    color = MaterialTheme.colorScheme.outline.copy(0.3f)
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = shape
                )
                .padding(16.dp),
            isLoading = isLoading,
            onRestoreClick = onRestoreClick,
            onPurchaseClick = onPurchaseClick
        )
    }

    // Discount Dialog
    if (showDiscountDialog && discountProduct != null && canShowDiscountDialog) {
        PurchaseDiscountDialog(
            discountProduct = discountProduct,
            discountPercentage = discountPercentage!!,
            onDismiss = {
                onProductSelected(products[0])
                showDiscountDialog = false
                onDismiss()
            },
            isLoading = isLoading,
            onAccept = {
                onProductSelected(discountProduct)
                onPurchaseClick()
            }
        )
    }
}

@Composable
private fun HeaderSection(isCompact: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Plan",
            style = if (isCompact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.paddingSmall))

        Text(
            text = "Unlock premium features and enhance your experience",
            style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProductsSection(
    products: List<StoreProduct>,
    selectedProduct: StoreProduct?,
    highlightedProductIndex: Int,
    isCompact: Boolean,
    onProductSelected: (StoreProduct) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
    ) {
        products.forEachIndexed { index, product ->
            ProductCard(
                product = product,
                isSelected = product == selectedProduct,
                isHighlighted = index == highlightedProductIndex,
                isCompact = isCompact,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onProductSelected(product) }
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: StoreProduct,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isCompact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(durationMillis = 200, easing = LinearEasing),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainer
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Box {
            // Highlight badge
            if (isHighlighted) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (-12).dp, y = (-4).dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(

                            text = "Best Value",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isCompact) Dimens.paddingLarge else Dimens.paddingExtraLarge)
                    .then(
                        if (isHighlighted)
                            Modifier.padding(
                                top = Dimens.paddingSmall
                            )
                        else Modifier
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - Product info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                    ) {
                        // Product title
                        Text(
                            text = product.title,
                            style = if (isCompact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface
                        )

                        // Description
                        product.localizedDescription?.let { description ->
                            Text(
                                text = description,
                                style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }

                        // Period info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                        ) {
                            Surface(
                                modifier = Modifier.size(if (isCompact) 16.dp else 20.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(if (isCompact) 8.dp else 10.dp),
                                        tint = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            if (product.period != null) {
                                val value = product.period!!.value
                                val period =
                                    product.period?.unit?.name?.lowercase()?.replaceFirstChar {
                                        it.uppercase()
                                    }
                                val text = "$value $period${if (value > 1) "s" else ""}".trim()
                                Text(
                                    text = text,
                                    style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "subscription",
                                    style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Right side - Price and selection
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
                    ) {
                        // Price
                        Text(
                            text = product.price.formatted,
                            style = if (isCompact) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.primary
                        )

                        // Selection indicator
                        if (isSelected) {
                            Surface(
                                modifier = Modifier.size(if (isCompact) 32.dp else 40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(if (isCompact) 16.dp else 20.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
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

@Composable
private fun FeaturesSection(
    features: List<PurchaseFeature>,
    isCompact: Boolean,
) {
    if (features.isEmpty())
        return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's Included",
            style = if (isCompact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
        ) {
            features.forEach { feature ->
                FeatureRow(
                    feature = feature,
                    isCompact = isCompact
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(
    feature: PurchaseFeature,
    isCompact: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
    ) {
        Surface(
            modifier = Modifier.size(if (isCompact) 24.dp else 32.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = feature.icon ?: Icons.Default.Star,
                    contentDescription = feature.label,
                    modifier = Modifier.size(if (isCompact) 12.dp else 16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Text(
            text = feature.label,
            style = if (isCompact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ActionButtonsSection(
    modifier: Modifier,
    isLoading: Boolean,
    onRestoreClick: () -> Unit,
    onPurchaseClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Primary action button
        Button(
            onClick = onPurchaseClick,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Dimens.paddingSmall))
            }
            Text(
                text = if (isLoading) "Processing..." else "Subscribe Now",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        // Restore button - centered
        TextButton(
            onClick = onRestoreClick,
            enabled = !isLoading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Restore Purchases",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TermsSection(
    terms: List<String>,
    isCompact: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Subscription Terms",
            style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.paddingMedium))

        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
        ) {
            terms.forEach { term ->
                Text(
                    text = "• $term",
                    style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}