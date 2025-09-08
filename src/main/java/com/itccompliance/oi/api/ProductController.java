package com.itccompliance.oi.api;

import com.itccompliance.oi.api.dto.ProductDtos.*;
import com.itccompliance.oi.domain.model.Product;
import com.itccompliance.oi.persistence.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateProductRequest req) {
        if (repo.findBySku(req.sku()).isPresent()) {
            return ResponseEntity.status(409).body(java.util.Map.of("error", "sku_exists"));
        }
        Product p = new Product(req.sku(), req.name(), req.price(), req.availableQuantity());
        p = repo.save(p);
        return ResponseEntity.status(201).body(new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getPrice(), p.getAvailableQuantity()));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<?> get(@PathVariable String sku) {
        return repo.findBySku(sku)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(new ProductResponse(p.getId(), p.getSku(), p.getName(), p.getPrice(), p.getAvailableQuantity())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> lowStock(@RequestParam int threshold) {
        return ResponseEntity.ok(repo.findByAvailableQuantityLessThan(threshold));
    }
}
