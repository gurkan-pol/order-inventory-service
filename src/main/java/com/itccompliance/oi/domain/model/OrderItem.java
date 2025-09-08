package com.itccompliance.oi.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Column(nullable = false)
  private String sku;

  @Column(nullable = false)
  private Integer quantity;

  protected OrderItem() {}
  public OrderItem(String sku, Integer quantity) {
    this.sku = sku; this.quantity = quantity;
  }

  public Long getId() { return id; }
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
  public String getSku() { return sku; }
  public Integer getQuantity() { return quantity; }
}
