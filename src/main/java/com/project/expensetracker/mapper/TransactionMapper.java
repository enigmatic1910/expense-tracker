package com.project.expensetracker.mapper;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.*;
import com.project.expensetracker.enums.TransactionType;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    //TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "paymentMode.id", target = "paymentModeId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target="transactionId", source="id")
    TransactionDto toTransactionDto(Transaction transaction);

    List<TransactionDto> toTransactionDtos(List<Transaction> transactions);

    @Mapping(target = "user", source = "userId", qualifiedByName = "idToUser")
    //@Mapping(target = "account", source = "transactionDto.accountId", qualifiedByName = "idToAccount")
    @Mapping(target="category", source = "transactionDto.categoryId", qualifiedByName = "idToCategory")
    @Mapping(target = "paymentMode", source = "transactionDto.paymentModeId", qualifiedByName = "idToPaymentMode")
    //@Mapping(target = "amount", source = "transactionDto", qualifiedByName = "mapAmount")
    @Mapping(target="amount", ignore = true)
    @Mapping(target="account", ignore = true)
    @Mapping(target="transferId", source="transferId")
    void transactionFromRequestDto(TransactionRequestDto transactionDto, @MappingTarget Transaction transaction, String userId, String transferId, boolean isSourceAccount);

    @AfterMapping
    default void mapAmountAndAccount(TransactionRequestDto dto, @MappingTarget Transaction transaction, boolean isSourceAccount){
        final var type = TransactionType.valueOf(dto.transactionType());

        if(type == TransactionType.TRANSFER){
            if(isSourceAccount){
               transaction.setAccount(Account.builder().id(dto.accountId()).build());
               transaction.setAmount(-dto.amount());
            }
            else{
                transaction.setAccount(Account.builder().id(dto.toAccount()).build());
                transaction.setAmount(dto.amount());
            }
            return;
        }
        transaction.setAccount(Account.builder().id(dto.accountId()).build());
        transaction.setAmount(TransactionType.valueOf(dto.transactionType()) == TransactionType.EXPENSE ? -dto.amount() : dto.amount());
    }

    @Named("idToUser")
    default User idToUser(String id){
        return id != null ? User.builder().id(id).build() : null;
    }


    @Named("idToCategory")
    default Category idToCategory(Long id){
        return id != null ? Category.builder().id(id).build() : null;
    }

    @Named("idToPaymentMode")
    default PaymentMode idToPaymentMode(Long id){
        return id != null ? PaymentMode.builder().id(id).build() : null;
    }
}
