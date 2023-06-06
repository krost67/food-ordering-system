package com.food.ordering.system.order.service.domain;

import static com.food.ordering.system.order.service.domain.TestData.CUSTOMER_ID;
import static com.food.ordering.system.order.service.domain.TestData.ORDER_ID;
import static com.food.ordering.system.order.service.domain.TestData.prepareCreateOrderCommand;
import static com.food.ordering.system.order.service.domain.TestData.prepareCreateOrderCommandWithWrongPrice;
import static com.food.ordering.system.order.service.domain.TestData.prepareCreateOrderCommandWithWrongProductPrice;
import static com.food.ordering.system.order.service.domain.TestData.prepareCustomer;
import static com.food.ordering.system.order.service.domain.TestData.prepareRestaurantResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeAll
    public void init() {
    }

    @Test
    public void createOrder_ShouldCreateOrder_WhenValidCreateOrderCommand() {
        // Given
        CreateOrderCommand createOrderCommand = prepareCreateOrderCommand();
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(prepareCustomer()));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(prepareRestaurantResponse(true)));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);

        // Then
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order created successfully.", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void createOrder_ShouldThrowOrderDomainException_WhenWrongOrderPrice() {
        // Given
        CreateOrderCommand createOrderCommandWrongPrice = prepareCreateOrderCommandWithWrongPrice();
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommandWrongPrice);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(prepareCustomer()));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommandWrongPrice)))
                .thenReturn(Optional.of(prepareRestaurantResponse(true)));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderDomainException orderDomainException = assertThrows(
                OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice)
        );

        // Then
        assertEquals("Total price [250.00] is not equal to Order items total [200.00]!", orderDomainException.getMessage());
    }

    @Test
    public void createOrder_ShouldThrowOrderDomainException_WhenWrongProductPrice() {
        // Given
        CreateOrderCommand createOrderCommandWrongProductPrice = prepareCreateOrderCommandWithWrongProductPrice();
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommandWrongProductPrice);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(prepareCustomer()));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommandWrongProductPrice)))
                .thenReturn(Optional.of(prepareRestaurantResponse(true)));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderDomainException orderDomainException = assertThrows(
                OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice)
        );

        // Then
        assertEquals("Order item price [60.00] is not valid for product [264966e2-b48b-4321-aa39-20d02a3a5c19]", orderDomainException.getMessage());
    }

    @Test
    public void createOrder_ShouldThrowOrderDomainException_WhenInactiveRestaurant() {
        // Given
        CreateOrderCommand createOrderCommand = prepareCreateOrderCommand();

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(prepareCustomer()));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(prepareRestaurantResponse(false)));

        // When
        OrderDomainException orderDomainException = assertThrows(
                OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand)
        );

        // Then
        assertEquals("Restaurant with id [909c9583-046f-4056-8742-27e544907262] is currently not active!", orderDomainException.getMessage());
    }
}
