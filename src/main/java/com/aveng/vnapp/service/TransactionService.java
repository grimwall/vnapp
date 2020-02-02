package com.aveng.vnapp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.domain.enums.TransactionType;
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
            .parallelStream()
            .map(entity -> transactionMapper.map(entity))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TransactionDTO> retrieveTransaction(String id) {
        return transactionRepository.findById(id)
            .map(entity -> transactionMapper.map(entity));
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> retrieveTransactionsByAppointmentId(String appointmentId) {

        return transactionRepository.findAllByAppointmentId(appointmentId)
            .stream()
            .map(entity -> transactionMapper.map(entity))
            .collect(Collectors.toList());
    }

    @Transactional
    public TransactionEntity createFinalizeTransaction(String appointmentId, String patientId, BigDecimal totalCost) {
        return transactionRepository.save(TransactionEntity.builder()
            .appointmentId(appointmentId)
            .patientId(patientId)
            .transactionDate(Instant.now())
            .type(TransactionType.FINALIZATION)
            .cost(totalCost)
            .build());
    }

    @Transactional
    public void createCancelTransaction(String appointmentId, String patientId, Instant startDate,
        BigDecimal cancelFee) {
        transactionRepository.save(TransactionEntity.builder()
            .appointmentId(appointmentId)
            .patientId(patientId)
            .transactionDate(startDate)
            .type(TransactionType.CANCELLATION)
            .cost(cancelFee)
            .build());
    }
}