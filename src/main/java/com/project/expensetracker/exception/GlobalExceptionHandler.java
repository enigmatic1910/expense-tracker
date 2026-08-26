package com.project.expensetracker.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?>  handleCategoryNotFoundException(CategoryNotFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(PaymentModeNotFoundException.class)
    public ResponseEntity<?> handlePaymentModeNotFoundException(PaymentModeNotFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<?> handleTransactionNotFoundException(TransactionNotFoundException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException(Exception ex){
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(500).body("An unexpected error occurred: ");
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<?> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(AccountNotOwnedByUserException.class)
    public ResponseEntity<?> handleAccountNotOwnedByUserException(AccountNotOwnedByUserException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(403).body(ex.getMessage());
    }

    @ExceptionHandler(UserConfigNotFoundException.class)
    public ResponseEntity<?> handleUserConfigNotFoundException(UserConfigNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
