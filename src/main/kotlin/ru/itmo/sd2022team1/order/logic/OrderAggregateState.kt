package ru.itmo.sd2022team1.order.logic

import ru.itmo.sd2022team1.order.api.*
import ru.itmo.sd2022team1.order.model.OrderStatus
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class OrderAggregateState : AggregateState<UUID, OrderAggregate> {
    private lateinit var orderId: UUID
    private lateinit var userId: UUID

    private var status: OrderStatus = OrderStatus.COLLECTING
    private var timeCreated: Long = System.currentTimeMillis()
    private var orderItemsAmount: MutableMap<UUID, Int> = mutableMapOf()

    override fun getId() = orderId
    fun getUserId() = userId

    fun getOrderItemsAmount() = orderItemsAmount

    fun getStatus() = status

    fun createOrder(auth: String): OrderCreatedEvent {
        val userId = UUID.fromString(auth) // todo get user from auth
        return OrderCreatedEvent(UUID.randomUUID(), userId)
    }

    fun addItemToOrder(orderId: UUID, itemId: UUID, amount: Int): ItemAddedToOrderEvent {
        return ItemAddedToOrderEvent(orderId, itemId, amount)
    }

    fun deleteItemFromOrder(orderId: UUID, itemId: UUID, amount: Int): ItemRemovedFromOrderEvent {
        return ItemRemovedFromOrderEvent(orderId, itemId, amount)
    }

    fun bookOrder(orderId: UUID): OrderBookedEvent {
        return OrderBookedEvent(orderId)
    }

    fun discardOrder(orderId: UUID): OrderBookingCanceledEvent {
        return OrderBookingCanceledEvent(orderId)
    }

    @StateTransitionFunc
    fun createOrder(event: OrderCreatedEvent) {
        orderId = event.orderId
        userId = event.userId
    }

    @StateTransitionFunc
    fun addItemToOrder(event: ItemAddedToOrderEvent) {
        orderItemsAmount[event.itemId] = orderItemsAmount.getOrDefault(event.itemId, 0) + event.amount
    }

    @StateTransitionFunc
    fun bookOrder(event: OrderBookedEvent) {
        status = OrderStatus.BOOKED
    }

    @StateTransitionFunc
    fun discardOrder(event: OrderBookingCanceledEvent) {
        status = OrderStatus.DISCARD
    }

    @StateTransitionFunc
    fun removeItem(event: ItemRemovedFromOrderEvent) {
        val bill =
            orderItemsAmount[event.itemId] ?: error("Item with id=${event.itemId} not found in order ${event.orderId}")
        require(event.itemCount <= bill)
        if (event.itemCount == bill) {
            orderItemsAmount.remove(event.itemId)
        } else {
            orderItemsAmount[event.itemId] = bill - event.itemCount
        }
    }
}
