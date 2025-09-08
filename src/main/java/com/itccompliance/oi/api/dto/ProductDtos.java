package com.itccompliance.oi.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductDtos {
  public record CreateProductRequest(
      @NotBlank String sku,
      @NotBlank String name,
      @NotNull @Positive BigDecimal price,
      @NotNull @PositiveOrZero Integer availableQuantity
  ) {}

  public record ProductResponse(Long id, String sku, String name, BigDecimal price, Integer availableQuantity) {}
}
