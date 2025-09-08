package com.itccompliance.oi.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itccompliance.oi.api.dto.ProductDtos.CreateProductRequest;
import com.itccompliance.oi.api.dto.OrderDtos.CreateOrderRequest;
import com.itccompliance.oi.api.dto.OrderDtos.OrderItemDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FulfilmentIT {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  Long orderId;

  @BeforeEach
  void setup() throws Exception {
    mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(new CreateProductRequest("SKU-X","Thing", new BigDecimal("1.00"), 10))))
        .andExpect(status().isCreated());

    var req = new CreateOrderRequest("x@y.com", List.of(new OrderItemDto("SKU-X", 1)));
    var res = mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andReturn();
    orderId = om.readTree(res.getResponse().getContentAsString()).get("id").asLong();
  }

  @Test
  void orderTransitionsToFulfilled() throws Exception {
    await().atMost(5, SECONDS).untilAsserted(() ->
      mvc.perform(get("/orders/" + orderId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("FULFILLED"))
    );
  }
}
