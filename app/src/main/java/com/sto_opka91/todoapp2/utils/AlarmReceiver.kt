package com.sto_opka91.todoapp2.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sto_opka91.todoapp.fragments.splash.SplashFragment
import com.sto_opka91.todoapp2.MainActivity
import com.sto_opka91.todoapp2.R

class AlarmReceiver: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        val todo = intent?.getStringExtra("todo")
        val taskId = intent?.getStringExtra("taskId")
        Log.d( "myLog", todo.toString())
        Log.d( "myLog", taskId.toString())
        val i = Intent(context,MainActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context,taskId.hashCode(),i, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context!!, "todoApp")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Reminder ToDo")
            .setContentText(todo)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)


        notificationManager.notify(taskId.hashCode(), builder.build())


    }
}