package com.itccompliance.oi.service;

import com.itccompliance.oi.domain.error.InsufficientStockException;
import com.itccompliance.oi.domain.model.*;
import com.itccompliance.oi.metrics.MetricsCollector;
import com.itccompliance.oi.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final FulfilmentService fulfilmentService;
    private final MetricsCollector metricsCollector;

    public OrderService(ProductRepository productRepo, OrderRepository orderRepo, FulfilmentService fulfilmentService, MetricsCollector metricsCollector) {
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.fulfilmentService = fulfilmentService;
        this.metricsCollector = metricsCollector;
    }

    @Transactional
    public Order createAndReserve(String customerEmail, List<OrderItem> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("items must not be empty");

        for (OrderItem it : items) {
            var product = productRepo.findBySkuForUpdate(it.getSku())
                    .orElseThrow(() -> new IllegalArgumentException("Unknown SKU: " + it.getSku()));
            if (it.getQuantity() <= 0)
                throw new IllegalArgumentException("quantity must be > 0 for sku " + it.getSku());
            if (product.getAvailableQuantity() < it.getQuantity()) {
                throw new InsufficientStockException("insufficient stock for sku " + it.getSku());
            }
        }

        for (OrderItem it : items) {
            var product = productRepo.findBySkuForUpdate(it.getSku()).orElseThrow();
            product.setAvailableQuantity(product.getAvailableQuantity() - it.getQuantity());
            metricsCollector.incrementStockReserved();
        }

        var order = new Order(customerEmail);
        items.forEach(order::addItem);
        order.setStatus(OrderStatus.RESERVED);
        var saved = orderRepo.save(order);
        metricsCollector.incrementOrdersCreated();
        fulfilmentService.fulfilAsync(order.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Order get(Long id) {
        return orderRepo.findById(id).orElseThrow();
    }
}
