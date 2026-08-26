package com.example.impulseguard.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.impulseguard.data.OverlayDecision
import com.example.impulseguard.ui.screens.InterceptionOverlayContent
import com.example.impulseguard.ui.theme.ImpulseGuardTheme

/** Draws the real interception pause screen as a [WindowManager] overlay on top
 * of whatever app the user just opened — requires SYSTEM_ALERT_WINDOW. Never
 * blocks: both actions, and the OS back gesture, simply dismiss it. */
object OverlayController {
    private var composeView: ComposeView? = null
    private var owner: OverlayLifecycleOwner? = null
    private var windowManager: WindowManager? = null

    fun show(context: Context, decision: OverlayDecision, streakCurrentRun: Int) {
        dismiss() // never stack two overlays

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val overlayOwner = OverlayLifecycleOwner().apply {
            performRestore()
            handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        val view = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setViewTreeLifecycleOwner(overlayOwner)
            setViewTreeViewModelStoreOwner(overlayOwner)
            setViewTreeSavedStateRegistryOwner(overlayOwner)
            setContent {
                ImpulseGuardTheme {
                    InterceptionOverlayContent(
                        appName = decision.appName,
                        initial = decision.initial,
                        colorRole = decision.colorRole,
                        opensThisMonth = decision.opensThisMonth,
                        spentThisMonth = decision.spentThisMonth,
                        lastPurchase = decision.lastPurchase,
                        streakCurrentRun = streakCurrentRun,
                        onContinuePlanned = { dismiss() },
                        onContinueBrowsing = { dismiss() },
                    )
                }
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        )

        runCatching { wm.addView(view, params) }.onFailure { return }
        overlayOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        overlayOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        composeView = view
        owner = overlayOwner
        windowManager = wm
    }

    fun dismiss() {
        val view = composeView ?: return
        owner?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        owner?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        owner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        runCatching { windowManager?.removeViewImmediate(view) }
        composeView = null
        owner = null
        windowManager = null
    }
}
