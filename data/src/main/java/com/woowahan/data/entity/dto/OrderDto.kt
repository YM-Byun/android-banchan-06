package com.woowahan.data.entity.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.woowahan.data.entity.table.OrderItemTableEntity
import com.woowahan.data.entity.table.OrderTableEntity

data class OrderDto(
    @Embedded
    val order: OrderTableEntity,
    @Relation(
        parentColumn = OrderTableEntity.COLUMN_ID,
        entityColumn = OrderItemTableEntity.COLUMN_ORDER_ID
    )
    var orderItemInforms: List<OrderItemTableEntity>
) {
    fun toEntity(): OrderEntity = OrderEntity(
        order.id.toLong(),
        order.time,
        orderItemInforms,
        order.deliveryState
    )
}

data class OrderEntity(
    val orderId: Long,
    val time: String,
    val items: List<OrderItemTableEntity>,
    val deliveryState: Boolean
)
