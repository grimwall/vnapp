package com.aveng.vnapp.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.repository.TransactionRepository;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.dto.enums.TransactionType;
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

    @Transactional(readOnly = true)
    public List<TransactionDTO> retrieveTransactionsByAppointmentId(String appointmentId) {

        return transactionRepository.findAllByAppointmentId(appointmentId)
            .stream()
            .map(entity -> transactionMapper.map(entity))
            .collect(Collectors.toList());
    }

    @Transactional
    public void createFinalizeTransaction(String appointmentId, String patientId, BigDecimal totalCost) {
        transactionRepository.save(TransactionEntity.builder()
            .appointmentId(appointmentId)
            .patientId(patientId)
            .transactionDate(OffsetDateTime.now())
            .type(TransactionType.FINALIZATION)
            .cost(totalCost)
            .build());
    }

    @Transactional
    public void createCancelTransaction(String appointmentId, String patientId, OffsetDateTime startDate,
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
