package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.service.dto.TransactionDTO;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionEntity map(TransactionDTO transactionDTO);

    TransactionDTO map(TransactionEntity transactionEntity);
}
