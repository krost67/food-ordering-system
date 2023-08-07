package com.food.ordering.system.payment.service.domain.exception;

import com.food.ordering.system.domain.exception.DomainException;

public class PaymentApplicationException extends DomainException {

    public PaymentApplicationException(String message) {
        super(message);
    }

    public PaymentApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
