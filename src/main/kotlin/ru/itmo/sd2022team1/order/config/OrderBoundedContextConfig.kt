package ru.itmo.sd2022team1.order.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.itmo.sd2022team1.order.api.OrderAggregate
import ru.itmo.sd2022team1.order.logic.OrderAggregateState
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import java.util.*

@Configuration
class OrderBoundedContextConfig {
    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun orderEsService(): EventSourcingService<UUID, OrderAggregate, OrderAggregateState> =
        eventSourcingServiceFactory.create()
}

