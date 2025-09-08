package com.itccompliance.oi.api;

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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper om;

  @BeforeEach
  void seedProduct() throws Exception {
    mvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
        .content(om.writeValueAsString(new CreateProductRequest("ABC-1","Widget", new BigDecimal("5.00"), 5))))
        .andExpect(status().isCreated());
  }

  @Test
  void postOrders_happyPath_201() throws Exception {
    var req = new CreateOrderRequest("c@d.com", List.of(new OrderItemDto("ABC-1", 2)));
    mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("RESERVED"));
  }

  @Test
  void postOrders_conflict_409_whenInsufficient() throws Exception {
    var req = new CreateOrderRequest("c@d.com", List.of(new OrderItemDto("ABC-1", 999)));
    mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(req)))
        .andExpect(status().isConflict());
  }
}
