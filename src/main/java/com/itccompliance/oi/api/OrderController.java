package com.itccompliance.oi.api;

import com.itccompliance.oi.api.dto.OrderDtos.*;
import com.itccompliance.oi.domain.model.Order;
import com.itccompliance.oi.domain.model.OrderItem;
import com.itccompliance.oi.domain.model.OrderStatus;
import com.itccompliance.oi.persistence.OrderRepository;
import com.itccompliance.oi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        var items = req.items().stream().map(i -> new OrderItem(i.sku(), i.quantity())).toList();
        Order order = orderService.createAndReserve(req.customerEmail(), items);
        var dto = new OrderResponse(order.getId(), order.getCustomerEmail(), order.getStatus().name(),
                order.getItems().stream().map(i -> new OrderItemDto(i.getSku(), i.getQuantity())).collect(Collectors.toList()));
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id) {
        try {
            Order order = orderService.get(id);
            var dto = new OrderResponse(order.getId(), order.getCustomerEmail(), order.getStatus().name(),
                    order.getItems().stream().map(i -> new OrderItemDto(i.getSku(), i.getQuantity())).toList());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
