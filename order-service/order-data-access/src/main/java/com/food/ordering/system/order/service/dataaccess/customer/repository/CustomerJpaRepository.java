package com.food.ordering.system.order.service.dataaccess.customer.repository;

import com.food.ordering.system.order.service.dataaccess.customer.entity.CustomerEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {

}
