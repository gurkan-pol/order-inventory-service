package com.itccompliance.oi.domain;

import com.itccompliance.oi.domain.error.InsufficientStockException;
import com.itccompliance.oi.domain.model.*;
import com.itccompliance.oi.domain.model.Order;
import com.itccompliance.oi.metrics.MetricsCollector;
import com.itccompliance.oi.persistence.*;
import com.itccompliance.oi.service.FulfilmentService;
import com.itccompliance.oi.service.OrderService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock ProductRepository productRepo;
  @Mock OrderRepository orderRepo;
  @Mock FulfilmentService fulfilmentService;
  @Mock MetricsCollector metricsCollector;

  OrderService service;

  @BeforeEach void setUp() { service = new OrderService(productRepo, orderRepo, fulfilmentService, metricsCollector); }

  @Test
  void reservesStock_atomically_and_decrements() {
    var p = new Product("SKU1", "N", new BigDecimal("9.99"), 10);
    when(productRepo.findBySkuForUpdate("SKU1")).thenReturn(Optional.of(p));
    when(orderRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    var order = service.createAndReserve("a@b.com", List.of(new OrderItem("SKU1", 3)));

    assertThat(p.getAvailableQuantity()).isEqualTo(7);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.RESERVED);
    verify(fulfilmentService).fulfilAsync(any());
  }

  @Test
  void insufficientStock_throws409_and_noPartialUpdates() {
    var p = new Product("SKU1", "N", new BigDecimal("9.99"), 2);
    when(productRepo.findBySkuForUpdate("SKU1")).thenReturn(Optional.of(p));

    assertThatThrownBy(() -> service.createAndReserve("a@b.com", List.of(new OrderItem("SKU1", 3))))
        .isInstanceOf(InsufficientStockException.class);

    assertThat(p.getAvailableQuantity()).isEqualTo(2); // unchanged
    verify(orderRepo, never()).save(any());
    verify(fulfilmentService, never()).fulfilAsync(anyLong());
  }
}
