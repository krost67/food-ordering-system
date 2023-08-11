package com.food.ordering.system.restaurant.service.domain.exception;

import com.food.ordering.system.domain.exception.DomainException;

public class RestaurantApplicationException extends DomainException {

    public RestaurantApplicationException(String message) {
        super(message);
    }

    public RestaurantApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
