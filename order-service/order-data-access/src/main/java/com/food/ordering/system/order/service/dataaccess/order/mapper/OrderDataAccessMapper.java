package com.food.ordering.system.order.service.dataaccess.order.mapper;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGES_DELIMITER;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .items(orderItemsToOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(
                        order.getFailureMessages() != null
                                ? String.join(FAILURE_MESSAGES_DELIMITER, order.getFailureMessages())
                                : Strings.EMPTY
                )
                .build();
        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .id(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages().isEmpty()
                                         ? new ArrayList<>()
                                         : new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGES_DELIMITER)))
                )
                .build();
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(itemEntity -> OrderItem.builder()
                        .id(new OrderItemId(itemEntity.getId()))
                        .product(new Product(new ProductId(itemEntity.getProductId())))
                        .price(new Money(itemEntity.getPrice()))
                        .quantity(itemEntity.getQuantity())
                        .subTotal(new Money(itemEntity.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return new StreetAddress(address.getId(),
                                 address.getStreet(),
                                 address.getPostalCode(),
                                 address.getCity());
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .productId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .city(deliveryAddress.getCity())
                .build();
    }
}
