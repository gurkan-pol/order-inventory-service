package com.itccompliance.oi.service;

import com.itccompliance.oi.domain.model.Order;
import com.itccompliance.oi.domain.model.OrderStatus;
import com.itccompliance.oi.persistence.OrderRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class FulfilmentService {
  private final OrderRepository orderRepo;
  public FulfilmentService(OrderRepository orderRepo) { this.orderRepo = orderRepo; }

  @Async
  @Transactional
  public void fulfilAsync(Long orderId) {
    try {
      Thread.sleep(ThreadLocalRandom.current().nextLong(100, 301));
      Order order = orderRepo.findById(orderId).orElseThrow();
      order.setStatus(OrderStatus.FULFILLED);
    } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
  }
}
