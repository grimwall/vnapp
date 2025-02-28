package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.util.OffsetDateTimeUtil;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring", uses = OffsetDateTimeUtil.class)
public interface TransactionMapper {

    TransactionEntity map(TransactionDTO transactionDTO);

    TransactionDTO map(TransactionEntity transactionEntity);
}
