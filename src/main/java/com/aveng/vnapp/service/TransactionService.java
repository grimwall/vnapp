package com.aveng.vnapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.repository.TransactionRepository;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.mapper.TransactionMapper;

/**
 * @author apaydin
 */
@Service
public class TransactionService {

    private TransactionMapper transactionMapper;
    private TransactionRepository transactionRepository;

    public TransactionService(TransactionMapper transactionMapper, TransactionRepository transactionRepository) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> retrieveTransactions() {

        return transactionRepository.findAll()
            .stream()
            .map(entity -> transactionMapper.map(entity))
            .collect(Collectors.toList());
    }
}
