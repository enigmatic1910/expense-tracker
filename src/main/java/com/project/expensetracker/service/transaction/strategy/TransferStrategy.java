package com.project.expensetracker.service.transaction.strategy;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.Transaction;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.TransactionNotFoundException;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferStrategy implements TransactionTypeStrategy {

    private final AccountService accountService;
    private final TransactionMapper transactionMapper;
    private final TransactionRepo transactionRepo;

    @Override
    public TransactionDto process(TransactionRequestDto requestBody, String userId, OperationType type) {
        Transaction debitTransaction;
        Transaction creditTransaction;
        String transferId;

        if(type == OperationType.UPDATE) {
            transferId = requestBody.transferId();
            if(Objects.isNull(transferId)){
                throw new RuntimeException("TransferId is null");
            }
            List<Transaction> transactions = transactionRepo.findAllByTransferId(requestBody.transferId());

            if(transactions.isEmpty() || transactions.size() < 2) {
                throw new RuntimeException("Transfer transactions not found for transferId: " + requestBody.transferId());
            }

            debitTransaction = transactions.stream()
                    .filter(t -> t.getAmount() < 0)
                    .findAny()
                    .orElseThrow(() -> new TransactionNotFoundException(requestBody.transactionId()));

            creditTransaction = transactions.stream()
                    .filter(t -> t.getAmount() > 0)
                    .findAny()
                    .orElseThrow(() -> new TransactionNotFoundException(requestBody.transactionId()));

            accountService.reverseBalance(debitTransaction.getAccount().getId(), debitTransaction.getAmount(), requestBody.paymentModeId(), requestBody.transactionType(), true);
            accountService.reverseBalance(creditTransaction.getAccount().getId(), creditTransaction.getAmount(), requestBody.paymentModeId(), requestBody.transactionType(), false);
        }
        else{
            debitTransaction = new Transaction();
            creditTransaction = new Transaction();
            transferId = UUID.randomUUID().toString();
        }

        accountService.updateBalance(requestBody.accountId(), requestBody.amount(), requestBody.paymentModeId(), requestBody.transactionType(), true);
        accountService.updateBalance(requestBody.toAccount(), requestBody.amount(), requestBody.paymentModeId(), requestBody.transactionType(), false);

        transactionMapper.transactionFromRequestDto(requestBody, debitTransaction, userId, transferId, true);
        transactionMapper.transactionFromRequestDto(requestBody, creditTransaction, userId, transferId, false);

        transactionRepo.save(debitTransaction);
        final var savedTransaction = transactionRepo.save(creditTransaction);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TRANSFER;
    }
}
