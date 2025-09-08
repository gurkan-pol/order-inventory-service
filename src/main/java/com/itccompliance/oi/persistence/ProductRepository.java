package com.itccompliance.oi.persistence;

import com.itccompliance.oi.domain.model.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findBySku(String sku);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Product p where p.sku = :sku")
  Optional<Product> findBySkuForUpdate(@Param("sku") String sku);

  List<Product> findByAvailableQuantityLessThan(int threshold);
}
