package com.example.leafly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WateringReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle boot completed to reschedule reminders if needed
    }
}
