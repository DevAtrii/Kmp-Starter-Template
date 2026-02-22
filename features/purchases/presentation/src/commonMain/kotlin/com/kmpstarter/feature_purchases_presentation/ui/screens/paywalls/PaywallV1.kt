/*
 *
 *  *
 *  *  * Copyright (c) 2026
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *  
 */

package com.kmpstarter.feature_purchases_presentation.ui.screens.paywalls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AppsOutage
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kmpstarter.core.events.enums.ThemeMode
import com.kmpstarter.core.platform.platform
import com.kmpstarter.feature_purchases_domain.models.Faq
import com.kmpstarter.feature_purchases_domain.models.PaywallMetadata
import com.kmpstarter.feature_purchases_domain.models.Product
import com.kmpstarter.feature_purchases_domain.models.ProductId
import com.kmpstarter.feature_purchases_domain.models.Review
import com.kmpstarter.feature_resources.Res
import com.kmpstarter.feature_resources.compose_multiplatform
import com.kmpstarter.feature_resources.paywall_v1_btn_continue
import com.kmpstarter.feature_resources.paywall_v1_btn_trial
import com.kmpstarter.feature_resources.paywall_v1_faqs_title
import com.kmpstarter.feature_resources.paywall_v1_feature_1
import com.kmpstarter.feature_resources.paywall_v1_feature_10
import com.kmpstarter.feature_resources.paywall_v1_feature_11
import com.kmpstarter.feature_resources.paywall_v1_feature_2
import com.kmpstarter.feature_resources.paywall_v1_feature_3
import com.kmpstarter.feature_resources.paywall_v1_feature_4
import com.kmpstarter.feature_resources.paywall_v1_feature_5
import com.kmpstarter.feature_resources.paywall_v1_feature_6
import com.kmpstarter.feature_resources.paywall_v1_feature_7
import com.kmpstarter.feature_resources.paywall_v1_feature_8
import com.kmpstarter.feature_resources.paywall_v1_feature_9
import com.kmpstarter.feature_resources.paywall_v1_purchasing
import com.kmpstarter.feature_resources.paywall_v1_restore_purchases
import com.kmpstarter.feature_resources.paywall_v1_reviews_title
import com.kmpstarter.feature_resources.paywall_v1_title
import com.kmpstarter.feature_resources.privacy_policy
import com.kmpstarter.feature_resources.starter_purchases_error_button_try_again
import com.kmpstarter.feature_resources.starter_purchases_products_not_found
import com.kmpstarter.feature_resources.terms_of_use
import com.kmpstarter.feature_resources.toActualString
import com.kmpstarter.ui_components.animations.FadeIn
import com.kmpstarter.ui_components.animations.FadeInTokens
import com.kmpstarter.ui_components.animations.Floating
import com.kmpstarter.ui_components.buttons.TimerCloseButton
import com.kmpstarter.ui_components.cards.SelectableListCard
import com.kmpstarter.ui_components.image.CoilImage
import com.kmpstarter.ui_components.lists.CupertinoLazyColumn
import com.kmpstarter.ui_layouts.empty.EmptyStateWithAction
import com.kmpstarter.ui_layouts.loading.LoadingLayout
import com.kmpstarter.ui_utils.color.fromHex
import com.kmpstarter.ui_utils.common_composables.HorizontalSpacer
import com.kmpstarter.ui_utils.common_composables.VerticalSpacer
import com.kmpstarter.ui_utils.composition_locals.LocalThemeMode
import com.kmpstarter.ui_utils.theme.Dimens
import com.kmpstarter.utils.variables.ifTrue
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

private object ScreenTokens {
    val CANCEL_MILLIS = if (platform.debug) 1000L else 5000L
    const val ERROR_DISMISS_MILLIS = 3000L
}

private data class PurchaseFeature(
    val icon: ImageVector,
    val text: StringResource,
)

private val purchaseFeatures = listOf(
    PurchaseFeature(Icons.Default.AccountTree, Res.string.paywall_v1_feature_1),
    PurchaseFeature(Icons.Default.Sync, Res.string.paywall_v1_feature_2),
    PurchaseFeature(Icons.Default.Storage, Res.string.paywall_v1_feature_3),
    PurchaseFeature(Icons.Default.Api, Res.string.paywall_v1_feature_4),
    PurchaseFeature(Icons.Default.BugReport, Res.string.paywall_v1_feature_5),
    PurchaseFeature(Icons.Default.Security, Res.string.paywall_v1_feature_6),
    PurchaseFeature(Icons.Default.Code, Res.string.paywall_v1_feature_7),
    PurchaseFeature(Icons.Default.Devices, Res.string.paywall_v1_feature_8),
    PurchaseFeature(Icons.Default.Update, Res.string.paywall_v1_feature_9),
    PurchaseFeature(Icons.Default.Extension, Res.string.paywall_v1_feature_10),
    PurchaseFeature(Icons.Default.RocketLaunch, Res.string.paywall_v1_feature_11),
)


@Composable
internal fun PaywallV1(
    paywallMetadata: PaywallMetadata,
    products: List<Product>,
    selectedProduct: Product? = null,
    isProductsLoading: Boolean,
    isRestoring: Boolean,
    isPurchasing: Boolean,
    onPurchaseClick: () -> Unit,
    onProductSelected: (Product) -> Unit,
    onGetProductsClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onCloseClick: () -> Unit,
) {
    Scaffold { innerPaddings ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .padding(Dimens.paddingMedium)
        ) {

            // handle loading states here
            if (isProductsLoading) {
                LoadingLayout()
                return@Box
            }

            if (products.isEmpty()) {
                EmptyStateWithAction(
                    heroIcon = Icons.Default.AppsOutage,
                    title = Res.string.starter_purchases_products_not_found.toActualString(),
                    description = "",
                    buttonText = Res.string.starter_purchases_error_button_try_again.toActualString(),
                    onClick = onGetProductsClick,
                )
                return@Box
            }

            // products are not null safe to handle
            FadeIn(
                delayMillis = FadeInTokens.DELAY_2,
                modifier = Modifier.zIndex(10f)
            ) {
                TimerCloseButton(
                    modifier = Modifier.align(Alignment.TopStart),
                    timeMillis = ScreenTokens.CANCEL_MILLIS,
                    onClick = onCloseClick
                )
            }
            CupertinoLazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    // hero icon
                    FadeIn(
                        delayMillis = FadeInTokens.DELAY_1
                    ) {
                        Floating {
                            Image(
                                modifier = Modifier.fillMaxWidth(0.75f).aspectRatio(1.2f),
                                painter = painterResource(Res.drawable.compose_multiplatform),
                                contentDescription = null
                            )
                        }
                    }
                    VerticalSpacer(Dimens.paddingMedium)
                }

                item {
                    // title
                    FadeIn(
                        delayMillis = FadeInTokens.DELAY_2
                    ) {
                        Text(
                            text = Res.string.paywall_v1_title.toActualString(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }

                    VerticalSpacer(Dimens.paddingSmall)
                }

                item {
                    // features

                    FadeIn(
                        delayMillis = FadeInTokens.DELAY_3
                    ) {
                        FeaturesSection()

                    }
                }

                // products section
                item {
                    ProductsSection(
                        modifier = Modifier.padding(
                            vertical = Dimens.paddingMedium
                        ),
                        selectedProductId = selectedProduct?.id,
                        onProductSelected = onProductSelected,
                        products = products,
                    )
                }




                item {
                    if (paywallMetadata.reviews.isNotEmpty()) {
                        FadeIn(
                            delayMillis = FadeInTokens.DELAY_5
                        ) {
                            ReviewsSection(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.paddingMedium),
                                reviews = paywallMetadata.reviews.toList()
                            )
                        }
                    }
                }

                item {
                    if (paywallMetadata.faqs.isNotEmpty()) {
                        FadeIn(
                            delayMillis = FadeInTokens.DELAY_6
                        ) {
                            FaqSection(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.paddingMedium),
                                faqs = paywallMetadata.faqs.toList()
                            )
                        }
                    }
                }

                // products section repeated
                item {
                    ProductsSectionV2(
                        modifier = Modifier.padding(
                            vertical = Dimens.paddingMedium
                        ),
                        delayMillis = FadeInTokens.DELAY_7,
                        selectedProductId = selectedProduct?.id,
                        onProductSelected = onProductSelected,
                        products = products,
                    )


                    VerticalSpacer(height = 100.dp)
                }
            }

            FadeIn(
                modifier = Modifier.align(Alignment.BottomCenter),
                delayMillis = FadeInTokens.DELAY_7
            ) {
                BottomSection(
                    isPurchasing = isPurchasing,
                    hasTrial = selectedProduct?.isTrial == true,
                    onPurchaseClick = onPurchaseClick,
                    onRestoreClick = onRestoreClick,
                    onPrivacyClick = onPrivacyClick,
                    onTermsClick = onTermsClick
                )
            }


        }
    }
    if (isRestoring) LoadingLayout()
}


@Composable
private fun FeaturesSection(
    modifier: Modifier = Modifier,
) {
    val cardHeight = 70.dp
    val padding = Dimens.paddingSmall
    val parentHeight = cardHeight * 2f + padding

    val (firstHalf, secondHalf) = remember {
        val mid = purchaseFeatures.size / 2
        val first = purchaseFeatures.subList(0, mid)
        val second = purchaseFeatures.subList(mid, purchaseFeatures.size)
        first to second
    }

    val maskWidth = 100.dp
    Box(
        modifier = modifier
    ) {

        Column(modifier = Modifier) {

            // top lazy row
            AutoScrollingLazyFeaturesRow(
                features = firstHalf,
                cardHeight = cardHeight,
                padding = padding
            )
            // bottom lazy row
            AutoScrollingLazyFeaturesRow(
                features = secondHalf,
                cardHeight = cardHeight,
                padding = padding,
                startInverse = true
            )

        }

        // masks
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        HorizontalBackgroundMaskedGradient(
            modifier = Modifier
                .align(
                    if (isLtr) Alignment.CenterStart else Alignment.CenterEnd
                )
                .width(maskWidth)
                .height(parentHeight)
        )

        HorizontalBackgroundMaskedGradient(
            modifier = Modifier
                .align(
                    if (isLtr) Alignment.CenterEnd else Alignment.CenterStart
                )
                .width(maskWidth)
                .height(parentHeight),
            inverse = true,
        )


    }
}

@Composable
private fun AutoScrollingLazyFeaturesRow(
    features: List<PurchaseFeature>,
    cardHeight: Dp,
    padding: Dp,
    startDelay: Long = 0L,
    startInverse: Boolean = false,
    delay: Long = 50L, // smaller = faster scroll
    defaultScrollX: Float = 0f,
) {
    val listState = rememberLazyListState()
    var scrollForward by remember { mutableStateOf(true) }

    // Disable user interaction
    Box(
        modifier = Modifier
            .pointerInput(Unit) {} // intercept touch events
    ) {
        LazyRow(
            state = listState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(features) { feature ->
                FeatureCard(
                    modifier = Modifier
                        .height(cardHeight)
                        .padding(
                            vertical = padding,
                            horizontal = Dimens.paddingExtraSmall
                        ),
                    feature = feature
                )
            }
        }
    }

    // Auto-scroll logic
    LaunchedEffect(features) {
        if (startInverse)
            listState.scrollToItem(index = features.lastIndex)
        delay(startDelay)
        // jump to default offset if specified
        if (defaultScrollX > 0f) {
            listState.scrollToItem(0, defaultScrollX.toInt())
        }

        while (true) {
            delay(delay)

            val maxOffset = listState.layoutInfo.totalItemsCount - 1
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val firstVisibleItem = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0

            if (scrollForward) {
                listState.scrollBy(2f)
                if (lastVisibleItem >= maxOffset) {
                    scrollForward = false
                }
            } else {
                listState.scrollBy(-2f)
                if (firstVisibleItem == 0 && listState.firstVisibleItemScrollOffset <= 0) {
                    scrollForward = true
                }
            }
        }
    }
}


@Composable
private fun HorizontalBackgroundMaskedGradient(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    inverse: Boolean = false,
    startX: Float = 0f,
) {
    val colors = if (inverse) {
        listOf(
            Color.Transparent,
            backgroundColor,
        )
    } else {
        listOf(
            backgroundColor,
            Color.Transparent,
        )
    }
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = colors,
                    startX = startX
                )
            )
    )
}

@Composable
private fun BottomSection(
    modifier: Modifier = Modifier,
    isPurchasing: Boolean,
    hasTrial: Boolean,
    onPurchaseClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
    ) {
        // CTA
        val text = if (isPurchasing) Res.string.paywall_v1_purchasing.toActualString() else null
        PurchaseCTA(
            enabled = !isPurchasing,
            isPurchasing = isPurchasing,
            text = text
                ?: if (hasTrial) Res.string.paywall_v1_btn_trial.toActualString() else Res.string.paywall_v1_btn_continue.toActualString(),
            onPurchaseClick = onPurchaseClick
        )
        // Legal
        LegalSection(
            onRestoreClick = onRestoreClick,
            onPrivacyClick = onPrivacyClick,
            onTermsClick = onTermsClick
        )
    }
}

@Composable
private fun ProductsSection(
    modifier: Modifier = Modifier,
    delayMillis: Long = FadeInTokens.DELAY_4,
    products: List<Product>,
    selectedProductId: ProductId?,
    onProductSelected: (Product) -> Unit,
) {


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
    ) {
        products.forEach { product ->
            FadeIn(
                delayMillis = delayMillis
            ) {
                ProductCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = product.title,
                    description = product.description.replace("%price%", product.price),
                    isSelected = product.id == selectedProductId,
                    badgeText = product.badge.text,
                    badgeBg = Color.fromHex(
                        hexString = product.badge.backgroundColor,
                        MaterialTheme.colorScheme.error
                    )
                        ?: MaterialTheme.colorScheme.error,
                    badgeType = ProductBadgeType.ADVANCE,
                    onClick = {
                        onProductSelected(product)
                    })
            }
        }

    }
}

private enum class ProductBadgeType {
    SIMPLE, ADVANCE
}

@Composable
private fun ProductCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isSelected: Boolean,
    badgeText: String,
    badgeBg: Color,
    badgeType: ProductBadgeType,
    onClick: () -> Unit,
) {
    SelectableListCard(
        modifier = modifier,
        isSelected = isSelected,
        onClick = onClick,
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().animateContentSize()
        ) {
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title, style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (badgeText.isNotEmpty())
                ProductBadge(
                    modifier = Modifier
                        .padding(end = Dimens.paddingExtraLarge),
                    badgeBg = badgeBg,
                    text = badgeText,
                    type = badgeType,
                )
        }
    }

}

@Composable
private fun ProductsSectionV2(
    modifier: Modifier = Modifier,
    delayMillis: Long = FadeInTokens.DELAY_4,
    products: List<Product>,
    selectedProductId: ProductId?,
    onProductSelected: (Product) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
    ) {
        products.forEach { product ->
            FadeIn(
                delayMillis = delayMillis
            ) {
                ProductCardV2(
                    modifier = Modifier.fillMaxWidth(),
                    title = product.title,
                    description = product.description.replace("%price%", product.price),
                    isSelected = product.id == selectedProductId,
                    badgeText = product.badge.text,
                    badgeBg = Color.fromHex(
                        hexString = product.badge.backgroundColor,
                        MaterialTheme.colorScheme.error
                    )
                        ?: MaterialTheme.colorScheme.error,
                    badgeType = ProductBadgeType.ADVANCE,
                    onClick = {
                        onProductSelected(product)
                    })
            }
        }

    }
}

@Composable
private fun ProductCardV2(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isSelected: Boolean,
    badgeText: String,
    badgeBg: Color,
    badgeType: ProductBadgeType,
    onClick: () -> Unit,
) {
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(
            alpha = 0.5f
        )
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val containerColor =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.paddingMedium))
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.paddingMedium),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (badgeText.isNotEmpty()) {
                    ProductBadge(
                        badgeBg = badgeBg,
                        text = badgeText,
                        type = badgeType,
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

}

@Composable
private fun ProductBadge(
    modifier: Modifier = Modifier,
    text: String,
    type: ProductBadgeType,
    badgeBg: Color,
) {
    Text(
        modifier = modifier.then(
            if (type == ProductBadgeType.ADVANCE) Modifier.clip(RoundedCornerShape(Dimens.paddingSmall))
                .background(badgeBg).padding(Dimens.paddingSmall)
            else Modifier
        ),
        text = text,
        style = if (type == ProductBadgeType.SIMPLE) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = if (type == ProductBadgeType.SIMPLE) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onError,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PurchaseCTA(
    modifier: Modifier = Modifier,
    text: String,
    isPurchasing: Boolean,
    enabled: Boolean,
    onPurchaseClick: () -> Unit,
) {
    Button(
        enabled = enabled,
        onClick = onPurchaseClick,
        modifier = modifier.fillMaxWidth().height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2ed136), contentColor = Color.White
        )
    ) {
        if (isPurchasing) {
            LoadingIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text, style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun LegalSection(
    modifier: Modifier = Modifier,
    onRestoreClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Text(
            modifier = Modifier.weight(1f).clickable {
                onRestoreClick()
            },
            text = Res.string.paywall_v1_restore_purchases.toActualString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )

        Text(
            modifier = Modifier.weight(1f).clickable {
                onPrivacyClick()
            },
            text = Res.string.privacy_policy.toActualString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )


        Text(
            modifier = Modifier.weight(1f).clickable {
                onTermsClick()
            },
            text = Res.string.terms_of_use.toActualString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )

    }
}

@Composable
private fun FeatureCard(
    modifier: Modifier = Modifier,
    feature: PurchaseFeature,
) {

    val containerColor = when (LocalThemeMode.current) {
        ThemeMode.LIGHT -> MaterialTheme.colorScheme.surface
        ThemeMode.DARK -> MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
        ThemeMode.SYSTEM -> isSystemInDarkTheme().ifTrue(
            MaterialTheme.colorScheme.surfaceColorAtElevation(
                3.dp
            )
        ) ?: MaterialTheme.colorScheme.surface
    }

    val contentColor = MaterialTheme.colorScheme.onSurface


    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = Dimens.paddingMedium)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingExtraSmall)
        ) {
            Icon(
                imageVector = feature.icon,
                tint = contentColor,
                modifier = Modifier.size(32.dp),
                contentDescription = null
            )

            Text(
                text = feature.text.toActualString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }

}

@Composable
private fun Feature(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    iconColor: Color = Color(0xFFED9D3A),
) {


    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            tint = iconColor,
            modifier = Modifier.size(24.dp),
            contentDescription = null
        )
        HorizontalSpacer(Dimens.paddingMedium)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start
        )
    }

}


@Composable
private fun ReviewsSection(
    modifier: Modifier = Modifier,
    reviews: List<Review>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
    ) {
        Text(
            text = Res.string.paywall_v1_reviews_title.toActualString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = Dimens.paddingSmall)
        )
        reviews.forEach { review ->
            ReviewCard(review = review)
        }

    }
}

@Composable
private fun ReviewCard(
    modifier: Modifier = Modifier,
    review: Review,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 1f)
        ),
        shape = RoundedCornerShape(Dimens.paddingMedium)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
            ) {
                CoilImage(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    url = review.profilePictureUrl,
                    contentDescription = null
                )
                Column {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < review.rating) Color(0xFFFFC107) else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = review.review,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FaqSection(
    modifier: Modifier = Modifier,
    faqs: List<Faq>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
    ) {
        Text(
            text = Res.string.paywall_v1_faqs_title.toActualString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = Dimens.paddingSmall)
                .padding(
                    bottom = Dimens.paddingSmall
                )
        )
        faqs.forEach { faq ->
            FaqItem(faq = faq)
        }
    }
}

@Composable
private fun FaqItem(
    modifier: Modifier = Modifier,
    faq: Faq,
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(Dimens.paddingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    VerticalSpacer(Dimens.paddingSmall)
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
