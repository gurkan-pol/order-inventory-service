# Order & Inventory Service

Run:
```
mvn -q -DskipTests=false test
mvn spring-boot:run
```

H2 console: http://localhost:8080/h2-console (JDBC URL jdbc:h2:mem:oi)

Sample requests (curl):

Create product
```
curl -s -X POST localhost:8080/products   -H 'Content-Type: application/json'   -d '{"sku":"W-1","name":"Widget","price":9.99,"availableQuantity":10}'
```

Create order
```
curl -s -X POST localhost:8080/orders   -H 'Content-Type: application/json'   -d '{"customerEmail":"a@b.com","items":[{"sku":"W-1","quantity":2}]}'
```

Get order
```
curl -s localhost:8080/orders/1 | jq
```

Get low stock products 
```
curl -s 'localhost:8080/products/low-stock?threshold=5' | jq
```

Metrics:

http://localhost:8080/actuator/metrics/orders.created
http://localhost:8080/actuator/metrics/orders.stock_reserved
