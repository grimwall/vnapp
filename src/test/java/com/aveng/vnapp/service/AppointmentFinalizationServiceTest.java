package com.aveng.vnapp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.domain.enums.AppointmentState;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentFinalizationServiceTest {

    public static final String MOCK_ID = "mockId";
    public static final String PATIENT_ID = "patientID";
    public static final String DOCTOR_ID = "doctorId";
    public static final String MOCK_TID = "mockTID";

    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorService doctorService;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AppointmentFinalizationService service;

    @Test
    public void finalize_success() {
        Instant now = Instant.now();

        AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.RESERVED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now)
            .endDate(now.plus(120, ChronoUnit.MINUTES))
            .build();

        entity.setId(MOCK_ID);

        DoctorDTO doctorDTO = DoctorDTO.builder().id(DOCTOR_ID).hourlyRate(BigDecimal.valueOf(10.00)).build();

        when(appointmentRepository.findById(eq(MOCK_ID))).thenReturn(Optional.ofNullable(entity));

        when(doctorService.retrieveDoctor(eq(DOCTOR_ID))).thenReturn(Optional.ofNullable(doctorDTO));

        ArgumentCaptor<BigDecimal> costAC = ArgumentCaptor.forClass(BigDecimal.class);

        TransactionEntity transactionEntity = TransactionEntity.builder().build();
        transactionEntity.setId(MOCK_TID);

        when(transactionService.createFinalizeTransaction(eq(MOCK_ID), eq(PATIENT_ID), costAC.capture())).thenReturn(
            transactionEntity);

        ArgumentCaptor<AppointmentEntity> appointmentEntityAC = ArgumentCaptor.forClass(AppointmentEntity.class);

        when(appointmentRepository.save(appointmentEntityAC.capture())).thenReturn(AppointmentEntity.builder().build());

        when(appointmentMapper.map(any(AppointmentEntity.class))).thenReturn(AppointmentDTO.builder().build());

        AppointmentDTO finalize = service.finalize(MOCK_ID);

        assertEquals(costAC.getValue(), BigDecimal.valueOf(20.00).setScale(2, BigDecimal.ROUND_HALF_UP));
        assertEquals(appointmentEntityAC.getValue().getState(), AppointmentState.FINALIZED);
        assertEquals(finalize.getTransactions().get(0), MOCK_TID);
    }

    @Test
    public void finalize_no_appointment() {

        when(appointmentRepository.findById(eq(MOCK_ID))).thenReturn(Optional.empty());

        try {
            service.finalize(MOCK_ID);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Cannot find the required appointment, finalization aborted");
        }

    }

    @Test
    public void finalize_not_correct_state() {
        Instant now = Instant.now();

        AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.CANCELLED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now)
            .endDate(now.plus(120, ChronoUnit.MINUTES))
            .build();

        entity.setId(MOCK_ID);

        when(appointmentRepository.findById(eq(MOCK_ID))).thenReturn(Optional.of(entity));

        try {
            service.finalize(MOCK_ID);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Appointment must be in RESERVED status!");
        }

    }

    @Test
    public void finalize_missing_doctor() {
        Instant now = Instant.now();

        AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.RESERVED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now)
            .endDate(now.plus(120, ChronoUnit.MINUTES))
            .build();

        entity.setId(MOCK_ID);

        when(appointmentRepository.findById(eq(MOCK_ID))).thenReturn(Optional.of(entity));

        when(doctorService.retrieveDoctor(eq(DOCTOR_ID))).thenReturn(Optional.empty());

        try {
            service.finalize(MOCK_ID);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Cannot find the required doctor");
        }

    }

}