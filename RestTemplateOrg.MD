```
src/main/java
├── com
│   └── example
│       └── microservice
│           ├── clients
│           │   ├── ProductServiceClient.java          // Interface
│           │   ├── OrderServiceClient.java            // Interface
│           │   └── impl
│           │       ├── ProductServiceClientImpl.java  // Implementation
│           │       └── OrderServiceClientImpl.java    // Implementation
│           ├── config
│           │   └── RestTemplateConfig.java
│           ├── dtos
│           │   ├── ProductResponse.java
│           │   └── OrderRequest.java
│           └── service
│               └── OrderProcessingService.java
```
```
package com.example.microservice.clients;

import com.example.microservice.dtos.ProductResponse;

public interface ProductServiceClient {
    ProductResponse getProduct(String productId);
    List<ProductResponse> getProductsByCategory(String category);
}

```
```
package com.example.microservice.clients.impl;

import com.example.microservice.clients.ProductServiceClient;
import com.example.microservice.dtos.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceClientImpl implements ProductServiceClient {

    private final RestTemplate restTemplate;

    public ProductServiceClientImpl(
        @Qualifier("productServiceTemplate") RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ProductResponse getProduct(String productId) {
        return restTemplate.getForObject(
            "/products/{id}", 
            ProductResponse.class, 
            productId
        );
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        ParameterizedTypeReference<List<ProductResponse>> responseType = 
            new ParameterizedTypeReference<>() {};
            
        return restTemplate.exchange(
            "/products?category={category}",
            HttpMethod.GET,
            null,
            responseType,
            category
        ).getBody();
    }
}

```

```
package com.example.microservice.clients;

import com.example.microservice.dtos.OrderRequest;
import com.example.microservice.dtos.OrderResponse;

public interface OrderServiceClient {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderStatus(String orderId);
}

```

```
```
package com.example.microservice.clients.impl;

import com.example.microservice.clients.OrderServiceClient;
import com.example.microservice.dtos.OrderRequest;
import com.example.microservice.dtos.OrderResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderServiceClientImpl implements OrderServiceClient {

    private final RestTemplate restTemplate;

    public OrderServiceClientImpl(
        @Qualifier("orderServiceTemplate") RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        return restTemplate.postForObject(
            "/orders", 
            request, 
            OrderResponse.class
        );
    }

    @Override
    public OrderResponse getOrderStatus(String orderId) {
        return restTemplate.getForObject(
            "/orders/{id}/status", 
            OrderResponse.class, 
            orderId
        );
    }
}

```
```
package com.example.microservice.service;

import com.example.microservice.clients.OrderServiceClient;
import com.example.microservice.clients.ProductServiceClient;
import com.example.microservice.dtos.OrderRequest;
import com.example.microservice.dtos.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    private final ProductServiceClient productClient;
    private final OrderServiceClient orderClient;

    public OrderProcessingService(
        ProductServiceClient productClient,
        OrderServiceClient orderClient
    ) {
        this.productClient = productClient;
        this.orderClient = orderClient;
    }

    public void processOrder(OrderRequest request) {
        ProductResponse product = productClient.getProduct(request.getProductId());
        orderClient.createOrder(request);
    }
}
```
