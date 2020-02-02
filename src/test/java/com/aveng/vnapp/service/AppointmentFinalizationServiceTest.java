package com.aveng.vnapp.service;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.aveng.vnapp.repository.AppointmentRepository;
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

    @Before
    public void setUp() {
    }

    @Test
    public void finalize_success() {
        Instant now = Instant.now();

        /*AppointmentEntity entity = AppointmentEntity.builder()
            .state(AppointmentState.RESERVED)
            .patientId(PATIENT_ID)
            .doctorId(DOCTOR_ID)
            .startDate(now)
            .endDate(now.plus(60, ChronoUnit.MINUTES))
            .build();

        entity.setId(MOCK_ID);

        DoctorDTO doctorDTO = DoctorDTO.builder()
            .id(DOCTOR_ID)
            .hourlyRate(BigDecimal.valueOf(10.00))
            .build();

        when(appointmentRepository.findById(eq(MOCK_ID)))
            .thenReturn(Optional.ofNullable(entity));

        when(doctorService.retrieveDoctor(eq(DOCTOR_ID)))
            .thenReturn(Optional.ofNullable(doctorDTO));

        ArgumentCaptor<BigDecimal> costAC = ArgumentCaptor.forClass(BigDecimal.class);

        TransactionEntity transactionEntity = TransactionEntity.builder().build();
        transactionEntity.setId(MOCK_TID);

        when(transactionService.createFinalizeTransaction(eq(MOCK_ID),eq(PATIENT_ID), costAC.capture()))
            .thenReturn(transactionEntity);*/
    }
}