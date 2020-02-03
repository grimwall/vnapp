package com.aveng.vnapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.domain.enums.TransactionType;
import com.aveng.vnapp.repository.TransactionRepository;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.mapper.TransactionMapper;
import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService service;

    @Test
    public void retrieveTransactions() {
        when(transactionRepository.findAll()).thenReturn(ImmutableList.of(TransactionEntity.builder().build()));
        when(transactionMapper.map(any(TransactionEntity.class))).thenReturn(TransactionDTO.builder().build());

        List<TransactionDTO> transactionDTOS = service.retrieveTransactions();

        assertEquals(1, transactionDTOS.size());
    }

    @Test
    public void retrieveTransaction() {
        when(transactionRepository.findById(anyString())).thenReturn(Optional.of(TransactionEntity.builder().build()));
        when(transactionMapper.map(any(TransactionEntity.class))).thenReturn(TransactionDTO.builder().build());

        assertTrue(service.retrieveTransaction("mock").isPresent());
    }

    @Test
    public void retrieveTransactionsByAppointmentId() {
        when(transactionRepository.findAllByAppointmentId(anyString())).thenReturn(
            ImmutableList.of(TransactionEntity.builder().build()));
        when(transactionMapper.map(any(TransactionEntity.class))).thenReturn(TransactionDTO.builder().build());

        List<TransactionDTO> transactionDTOS = service.retrieveTransactionsByAppointmentId("mock");

        assertEquals(1, transactionDTOS.size());
    }

    @Test
    public void createFinalizeTransaction() {

        String appointmentId = "appoId";
        String patientId = "patoId";
        BigDecimal totalCost = BigDecimal.ONE;

        ArgumentCaptor<TransactionEntity> finalTransactionAC = ArgumentCaptor.forClass(TransactionEntity.class);

        service.createFinalizeTransaction(appointmentId, patientId, totalCost);
        verify(transactionRepository, times(1)).save(finalTransactionAC.capture());

        TransactionEntity capturedTran = finalTransactionAC.getValue();

        assertEquals(capturedTran.getAppointmentId(), appointmentId);
        assertEquals(capturedTran.getPatientId(), patientId);
        assertEquals(capturedTran.getCost(), totalCost);
        assertEquals(capturedTran.getType(), TransactionType.FINALIZATION);
    }

    @Test
    public void createCancelTransaction() {

        String appointmentId = "appoId";
        String patientId = "patoId";
        Instant startDate = Instant.now();
        BigDecimal cancelFee = BigDecimal.ONE;

        ArgumentCaptor<TransactionEntity> canceTransactionAC = ArgumentCaptor.forClass(TransactionEntity.class);

        service.createCancelTransaction(appointmentId, patientId, startDate, cancelFee);
        verify(transactionRepository, times(1)).save(canceTransactionAC.capture());

        TransactionEntity capturedTran = canceTransactionAC.getValue();

        assertEquals(capturedTran.getAppointmentId(), appointmentId);
        assertEquals(capturedTran.getPatientId(), patientId);
        assertEquals(capturedTran.getCost(), cancelFee);
        assertEquals(capturedTran.getTransactionDate(), startDate);
        assertEquals(capturedTran.getType(), TransactionType.CANCELLATION);
    }
}