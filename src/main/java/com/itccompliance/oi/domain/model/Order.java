package com.itccompliance.oi.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String customerEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status = OrderStatus.NEW;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<OrderItem> items = new ArrayList<>();

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected Order() {}
  public Order(String customerEmail) { this.customerEmail = customerEmail; }

  public void addItem(OrderItem item) {
    item.setOrder(this);
    items.add(item);
  }

  public Long getId() { return id; }
  public String getCustomerEmail() { return customerEmail; }
  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }
  public List<OrderItem> getItems() { return items; }
  public Instant getCreatedAt() { return createdAt; }
}
