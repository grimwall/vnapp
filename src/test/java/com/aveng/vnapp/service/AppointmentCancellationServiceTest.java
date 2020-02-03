package com.aveng.vnapp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.domain.enums.AppointmentState;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;
import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentCancellationServiceTest {

    public static final String APPOINTMENT_ID = "mockId";
    public static final String PATIENT_ID = "patientID";
    public static final String DOCTOR_ID = "doctorId";
    public static final String MOCK_TID = "mockTID";

    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AppointmentCancellationService service;

    @Test
    public void cancel_success_with_late_fee() {
        Instant now = Instant.now();

        AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.FINALIZED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now)
            .endDate(now.plus(120, ChronoUnit.MINUTES))
            .build();

        entity.setId(APPOINTMENT_ID);

        when(appointmentRepository.findById(eq(APPOINTMENT_ID))).thenReturn(Optional.ofNullable(entity));

        TransactionDTO finalizationTransaction =
            TransactionDTO.builder().cost(BigDecimal.valueOf(100.00).setScale(2, BigDecimal.ROUND_HALF_UP)).build();

        ArgumentCaptor<BigDecimal> costAC = ArgumentCaptor.forClass(BigDecimal.class);

        ArgumentCaptor<AppointmentEntity> appointmentEntityAC = ArgumentCaptor.forClass(AppointmentEntity.class);

        when(appointmentRepository.save(appointmentEntityAC.capture())).thenReturn(AppointmentEntity.builder().build());

        when(appointmentMapper.map(any(AppointmentEntity.class))).thenReturn(
            AppointmentDTO.builder().id(APPOINTMENT_ID).build());

        ImmutableList<TransactionDTO> mockTransactionDTOs =
            ImmutableList.of(TransactionDTO.builder().id("mockTrans1id").build(),
                TransactionDTO.builder().id("mocktrans2id").build());

        when(transactionService.retrieveTransactionsByAppointmentId(eq(APPOINTMENT_ID))).thenReturn(
            Collections.singletonList(finalizationTransaction)).thenReturn(mockTransactionDTOs);

        AppointmentDTO appointmentDTO = service.cancel(APPOINTMENT_ID);

        assertTrue(appointmentDTO.getTransactions().containsAll(ImmutableList.of("mockTrans1id", "mocktrans2id")));
        assertEquals(appointmentEntityAC.getValue().getState(), AppointmentState.CANCELLED);
        verify(transactionService, times(1)).createCancelTransaction(eq(APPOINTMENT_ID), eq(PATIENT_ID),
            eq(entity.getStartDate()), costAC.capture());

        assertEquals(costAC.getValue(), BigDecimal.valueOf(25.00).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    public void cancel_success_with_no_late_fee() {
        Instant now = Instant.now();

        AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.FINALIZED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now.plus(62, ChronoUnit.MINUTES))
            .endDate(now.plus(120, ChronoUnit.MINUTES))
            .build();

        entity.setId(APPOINTMENT_ID);

        when(appointmentRepository.findById(eq(APPOINTMENT_ID))).thenReturn(Optional.ofNullable(entity));

        ArgumentCaptor<AppointmentEntity> appointmentEntityAC = ArgumentCaptor.forClass(AppointmentEntity.class);

        when(appointmentRepository.save(appointmentEntityAC.capture())).thenReturn(AppointmentEntity.builder().build());

        when(appointmentMapper.map(any(AppointmentEntity.class))).thenReturn(
            AppointmentDTO.builder().id(APPOINTMENT_ID).build());

        ImmutableList<TransactionDTO> mockTransactionDTOs =
            ImmutableList.of(TransactionDTO.builder().id("mockTrans1id").build());

        when(transactionService.retrieveTransactionsByAppointmentId(eq(APPOINTMENT_ID))).thenReturn(
            mockTransactionDTOs);

        AppointmentDTO appointmentDTO = service.cancel(APPOINTMENT_ID);

        assertEquals(appointmentEntityAC.getValue().getState(), AppointmentState.CANCELLED);

        assertTrue(appointmentDTO.getTransactions().containsAll(ImmutableList.of("mockTrans1id")));

        verify(transactionService, never()).createCancelTransaction(anyString(), anyString(), any(Instant.class),
            any(BigDecimal.class));
    }

    @Test
    public void cancel_fail_no_appointment() {

        when(appointmentRepository.findById(eq(APPOINTMENT_ID))).thenReturn(Optional.empty());

        try {
            service.cancel(APPOINTMENT_ID);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Cannot find the required appointment, cancel aborted");
        }
    }

    @Test
    public void cancel_fail_invalid_appointment_state() {

        Instant now = Instant.now();

        AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.RESERVED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now.plus(62, ChronoUnit.MINUTES))
            .endDate(now.plus(120, ChronoUnit.MINUTES))
            .build();

        entity.setId(APPOINTMENT_ID);

        when(appointmentRepository.findById(eq(APPOINTMENT_ID))).thenReturn(Optional.of(entity));

        try {
            service.cancel(APPOINTMENT_ID);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "You can only cancel FINALIZED appointments!");
        }
    }
}