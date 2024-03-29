package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.food.ordering.system.domain.utils.DomainConstants.UTC;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentEvent validateAndInitializePayment(Payment payment,
                                                     CreditEntry creditEntry,
                                                     List<CreditHistory> creditHistories,
                                                     List<String> failureMessages,
                                                     DomainEventPublisher<PaymentCompletedEvent> completedEventPublisher,
                                                     DomainEventPublisher<PaymentFailedEvent> failedEventPublisher) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();

        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);

        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment is initiated for order id [{}]", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), completedEventPublisher);
        } else {
            log.info("Payment initiation is failed for order if [{}]", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failedEventPublisher, failureMessages);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment,
                                                 CreditEntry creditEntry,
                                                 List<CreditHistory> creditHistories,
                                                 List<String> failureMessages,
                                                 DomainEventPublisher<PaymentCancelledEvent> cancelledEventPublisher,
                                                 DomainEventPublisher<PaymentFailedEvent> failedEventPublisher) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment is cancelled for order id [{}]", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), cancelledEventPublisher);
        } else {
            log.info("Payment cancellation is failed for order id [{}]", payment.getOrderId().getValue());
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failedEventPublisher, failureMessages);
        }
    }

    private void validateCreditEntry(Payment payment,
                                     CreditEntry creditEntry,
                                     List<String> failureMessages) {
        if (payment.getPrice().isGreater(creditEntry.getTotalCreditAmount())) {
            log.error("Customer with id [{}] doesn't have enough credit for payment!",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add(String.format("Customer with id [%s] doesn't have enough credit for payment!",
                    creditEntry.getCustomerId().getValue()));
        }
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.addCreditAmount(payment.getPrice());
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(Payment payment,
                                     List<CreditHistory> creditHistories,
                                     TransactionType transactionType) {
        creditHistories.add(CreditHistory.builder()
                .id(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .transactionType(transactionType)
                .amount(payment.getPrice())
                .build());
    }

    private void validateCreditHistory(CreditEntry creditEntry,
                                       List<CreditHistory> creditHistories,
                                       List<String> failureMessages) {
        Money totalCreditHistory = getTotalCreditHistoryAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistory = getTotalCreditHistoryAmount(creditHistories, TransactionType.DEBIT);

        if (totalDebitHistory.isGreater(totalCreditHistory)) {
            log.error("Customer with id [{}] doesn't have enough credit according to credit history!",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add(String.format("Customer with id [%s] doesn't have enough credit according to credit history!",
                    creditEntry.getCustomerId().getValue()));
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            log.error("Credit history total is not equal to current credit for customer id [{}] !",
                    creditEntry.getCustomerId().getValue());
            failureMessages.add(String.format("Credit history total is not equal to current credit for customer id [%s] !",
                    creditEntry.getCustomerId().getValue()));
        }
    }

    private Money getTotalCreditHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> transactionType.equals(creditHistory.getTransactionType()))
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }
}
