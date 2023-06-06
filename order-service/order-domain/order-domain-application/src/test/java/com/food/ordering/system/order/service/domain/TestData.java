package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestData {

    public static final UUID CUSTOMER_ID = UUID.fromString("df160ee0-e7ae-478f-96d9-6c559bfa9054");
    public static final UUID RESTAURANT_ID = UUID.fromString("909c9583-046f-4056-8742-27e544907262");
    public static final UUID PRODUCT_ID = UUID.fromString("264966e2-b48b-4321-aa39-20d02a3a5c19");
    public static final UUID SECOND_PRODUCT_ID = UUID.fromString("364966e2-b48b-4321-aa39-20d02a3a5c19");
    public static final UUID ORDER_ID = UUID.fromString("264966e2-b48b-4321-aa39-20d02a3a5c19");
    public static final BigDecimal PRICE = new BigDecimal("200.00");

    public static CreateOrderCommand prepareCreateOrderCommand() {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                                 .street("Test street")
                                 .postalCode("0000AB")
                                 .city("Kyiv")
                                 .build())
                .price(PRICE)
                .items(List.of(OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(1)
                                       .price(new BigDecimal("50.00"))
                                       .subTotal(new BigDecimal("50.00"))
                                       .build(),
                               OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(3)
                                       .price(new BigDecimal("50.00"))
                                       .subTotal(new BigDecimal("150.00"))
                                       .build()))
                .build();
    }

    public static CreateOrderCommand prepareCreateOrderCommandWithWrongPrice() {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                                 .street("Test street")
                                 .postalCode("0000AB")
                                 .city("Kyiv")
                                 .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(1)
                                       .price(new BigDecimal("50.00"))
                                       .subTotal(new BigDecimal("50.00"))
                                       .build(),
                               OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(3)
                                       .price(new BigDecimal("50.00"))
                                       .subTotal(new BigDecimal("150.00"))
                                       .build()))
                .build();
    }

    public static CreateOrderCommand prepareCreateOrderCommandWithWrongProductPrice() {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                                 .street("Test street")
                                 .postalCode("0000AB")
                                 .city("Kyiv")
                                 .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(1)
                                       .price(new BigDecimal("60.00"))
                                       .subTotal(new BigDecimal("60.00"))
                                       .build(),
                               OrderItem.builder()
                                       .productId(PRODUCT_ID)
                                       .quantity(3)
                                       .price(new BigDecimal("50.00"))
                                       .subTotal(new BigDecimal("150.00"))
                                       .build()))
                .build();
    }

    public static Customer prepareCustomer() {
        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        return customer;
    }

    public static Restaurant prepareRestaurantResponse(boolean active) {
        return Restaurant.builder()
                .id(new RestaurantId(RESTAURANT_ID))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(SECOND_PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(active)
                .build();
    }



}
