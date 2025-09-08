package com.itccompliance.oi.api.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class OrderDtos {
  public record CreateOrderRequest(
      @Email @NotBlank String customerEmail,
      @NotEmpty List<OrderItemDto> items
  ) {}

  public record OrderItemDto(@NotBlank String sku, @NotNull @Positive Integer quantity) {}

  public record OrderResponse(Long id, String customerEmail, String status, List<OrderItemDto> items) {}
}
