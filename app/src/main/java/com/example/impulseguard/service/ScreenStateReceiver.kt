package com.example.impulseguard.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Gates polling to only run while the screen is on — the battery-conscious
 * behavior called out in the spec. */
class ScreenStateReceiver(
    private val onScreenOn: () -> Unit,
    private val onScreenOff: () -> Unit,
) : BroadcastReceiver() {
    constructor() : this(onScreenOn = {}, onScreenOff = {})

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> onScreenOn()
            Intent.ACTION_SCREEN_OFF -> onScreenOff()
        }
    }
}
