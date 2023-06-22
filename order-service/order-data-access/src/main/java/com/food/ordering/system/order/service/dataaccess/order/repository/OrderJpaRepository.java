package com.food.ordering.system.order.service.dataaccess.order.repository;

import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByTrackingId(UUID trackingId);
}
