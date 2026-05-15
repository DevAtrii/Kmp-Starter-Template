package com.kmpstarter.ui_utils.providers

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

/**
 * Provides Android Activity-backed CompositionLocals for Compose tree.
 *
 * Supplies:
 * - LocalActivityResultRegistryOwner (for ActivityResult APIs)
 * - LocalActivity (ComponentActivity access)
 * - LocalContext (Activity context override)
 *
 * Required for any composable that uses:
 * - rememberLauncherForActivityResult
 * - permissions / intents / external activity contracts
 *
 * Must wrap UI at screen root level. Avoid placing inside Lazy lists or frequently
 * recomposing subtrees to prevent unstable registry ownership.
 */
@Suppress("ParamsComparedByRef")
@Composable
fun ProvideActivityScope(activity: ComponentActivity, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalActivityResultRegistryOwner provides activity,
        LocalActivity provides activity,
        LocalContext provides activity
    ) {
        content()
    }
}

