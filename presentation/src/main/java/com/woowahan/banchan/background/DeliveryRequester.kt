package com.woowahan.banchan.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.woowahan.banchan.R
import java.util.*

object DeliveryRequester {
    private const val testDeliveryMill: Long = 1000*15

    fun setDeliveryAlarm(
        context: Context,
        orderId: Long,
        orderTitle: String?,
        orderItemCount: Int,
        minute: Int
    ) {
        val title = when(orderItemCount > 1){
            true -> context.getString(R.string.order_items_title, orderTitle ?: context.getString(R.string.order_default_product), orderItemCount-1)
            else -> orderTitle ?: context.getString(R.string.order_default_product)
        }
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().time.time + (minute * 60) * 1000,
            PendingIntent.getBroadcast(
                context,
                orderId.toInt(),
                DeliveryAlarmReceiver.get(context, orderId, title),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }
}