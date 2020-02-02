package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.util.DateUtil;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring", uses = DateUtil.class)
public interface TransactionMapper {

    TransactionEntity map(TransactionDTO transactionDTO);

    TransactionDTO map(TransactionEntity transactionEntity);
}
