package com.project.expensetracker.mapper;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.*;
import com.project.expensetracker.enums.TransactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    //TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "paymentMode.id", target = "paymentModeId")
    @Mapping(source = "category.id", target = "categoryId")
    TransactionDto toTransactionDto(Transaction transaction);

    List<TransactionDto> toTransactionDtos(List<Transaction> transactions);

    @Mapping(target = "user", source = "userId", qualifiedByName = "idToUser")
    @Mapping(target = "account", source = "transactionDto.accountId", qualifiedByName = "idToAccount")
    @Mapping(target="category", source = "transactionDto.categoryId", qualifiedByName = "idToCategory")
    @Mapping(target = "paymentMode", source = "transactionDto.paymentModeId", qualifiedByName = "idToPaymentMode")
    @Mapping(target = "amount", source = "transactionDto", qualifiedByName = "mapAmount")
    void transactionFromRequestDto(TransactionRequestDto transactionDto, @MappingTarget Transaction transaction, String userId);

    @Named("mapAmount")
    default Double mapAmount(TransactionRequestDto dto) {
        return TransactionType.valueOf(dto.transactionType()) == TransactionType.EXPENSE ? -dto.amount() : dto.amount();
    }

    @Named("idToUser")
    default User idToUser(String id){
        return id != null ? User.builder().id(id).build() : null;
    }

    @Named("idToAccount")
    default Account idToAccount(Long id){
        return id != null ? Account.builder().id(id).build() : null;
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
