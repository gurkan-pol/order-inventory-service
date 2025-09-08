package com.itccompliance.oi.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {

    private final Counter orderCreated;
    private final Counter stockReserved;

    public MetricsCollector(MeterRegistry registry) {
        this.orderCreated = Counter.builder("orders.created").register(registry);
        this.stockReserved = Counter.builder("orders.stock_reserved").register(registry);
    }

    public void incrementOrdersCreated() {
        orderCreated.increment();
    }

    public void incrementStockReserved() {
        stockReserved.increment();
    }
}
