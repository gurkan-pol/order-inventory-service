package com.itccompliance.oi.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private String sku;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private Integer availableQuantity;

  @Version
  private Long version;

  protected Product() {}

  public Product(String sku, String name, BigDecimal price, Integer availableQuantity) {
    this.sku = sku;
    this.name = name;
    this.price = price;
    this.availableQuantity = availableQuantity;
  }

  public Long getId() { return id; }
  public String getSku() { return sku; }
  public String getName() { return name; }
  public java.math.BigDecimal getPrice() { return price; }
  public Integer getAvailableQuantity() { return availableQuantity; }
  public void setName(String name) { this.name = name; }
  public void setPrice(java.math.BigDecimal price) { this.price = price; }
  public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
}
