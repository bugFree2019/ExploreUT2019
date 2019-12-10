package com.exploreutapp

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.exploreutapp.model.Place
import com.google.firebase.messaging.RemoteMessage
import com.pusher.pushnotifications.fcm.MessagingService
import java.io.Serializable

class NotificationsMessagingService : MessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.data.get("click_action")!=null) {
            if (remoteMessage.data.get("click_action").equals("ViewPlaceActivity")){
                showNewReportNotification(remoteMessage.data);
            }
        }
    }

    private fun showNewReportNotification(data: Map<String, String>) {

        val intent = Intent(applicationContext, ViewPlaceActivity::class.java)
        val place_id = data.get("place_id")!!
        val place_name = data.get("place_name")!!
        var place: Place = Place()
        place._id = place_id
        place.name = place_name
        Log.d("showname",place.name)
        Log.d("showname",place_id)
        intent.putExtra("place_to_show", place as Serializable)
        // intent.putExtra("place_name",place.name)
        val pendingIntent = PendingIntent.getActivity(applicationContext, (Math.random()*100).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, "events")
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        (application as EventsApplication).notificationManager.notify(0, notification.build())
    }
}