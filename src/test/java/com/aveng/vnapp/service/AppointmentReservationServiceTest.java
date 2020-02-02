package com.aveng.vnapp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.domain.enums.AppointmentState;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.dto.PatientDTO;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentReservationServiceTest {

    public static final String DOCTOR_ID = "doctorID";
    public static final String PATIENT_ID = "patientID";
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorService doctorService;
    @Mock
    private PatientService patientService;

    @InjectMocks
    private AppointmentReservationService service;

    @Before
    public void setUp() {
        when(doctorService.retrieveDoctor(eq(DOCTOR_ID))).thenReturn(
            Optional.of(DoctorDTO.builder().id(DOCTOR_ID).build()));

        when(patientService.retrievePatient(eq(PATIENT_ID))).thenReturn(
            Optional.of(PatientDTO.builder().id(PATIENT_ID).build()));

        when(appointmentRepository.findAllConflictingValidEvents(any(Instant.class), any(Instant.class), eq(DOCTOR_ID)))
            .thenReturn(Collections.emptyList());

        when(appointmentMapper.map(any(AppointmentEntity.class))).thenReturn(AppointmentDTO.builder().build());
    }

    @Test
    public void reserve_success() {

        ArgumentCaptor<AppointmentEntity> appointmentEntityAC = ArgumentCaptor.forClass(AppointmentEntity.class);

        when(appointmentRepository.save(appointmentEntityAC.capture())).thenReturn(AppointmentEntity.builder().build());

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        AppointmentDTO requestDTO =
            AppointmentDTO.builder().doctorId(DOCTOR_ID).patientId(PATIENT_ID).startDate(now).duration(60).build();

        AppointmentDTO reserve = service.reserve(requestDTO);

        assertEquals(reserve, AppointmentDTO.builder().build());
        AppointmentEntity captured = appointmentEntityAC.getValue();

        assertEquals(captured.getDoctorId(), DOCTOR_ID);
        assertEquals(captured.getPatientId(), PATIENT_ID);
        assertEquals(captured.getState(), AppointmentState.RESERVED);

    }

    @Test
    public void reserve_doctor_missing() {

        when(doctorService.retrieveDoctor(eq(DOCTOR_ID))).thenReturn(Optional.empty());

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        AppointmentDTO requestDTO =
            AppointmentDTO.builder().doctorId(DOCTOR_ID).patientId(PATIENT_ID).startDate(now).duration(60).build();

        try {
            service.reserve(requestDTO);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Cannot find the required doctor");
        }
    }

    @Test
    public void reserve_patient_missing() {

        when(patientService.retrievePatient(eq(PATIENT_ID))).thenReturn(Optional.empty());

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        AppointmentDTO requestDTO =
            AppointmentDTO.builder().doctorId(DOCTOR_ID).patientId(PATIENT_ID).startDate(now).duration(60).build();

        try {
            service.reserve(requestDTO);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Cannot find the required patient");
        }
    }

    @Test
    public void reserve_conflicting_appointment() {

        when(appointmentRepository.findAllConflictingValidEvents(any(Instant.class), any(Instant.class), eq(DOCTOR_ID)))
            .thenReturn(Collections.singletonList(AppointmentEntity.builder().build()));

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        AppointmentDTO requestDTO =
            AppointmentDTO.builder().doctorId(DOCTOR_ID).patientId(PATIENT_ID).startDate(now).duration(60).build();

        try {
            service.reserve(requestDTO);
        } catch (ApplicationException e) {
            assertEquals(e.getLevel(), Level.INFO);
            assertEquals(e.getStatus(), HttpStatus.BAD_REQUEST.value());
            assertEquals(e.getMessage(), "Appointment conflicts with previous appointments");
        }

    }
}