package com.aveng.vnapp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.AppointmentSearchRequest;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private AppointmentReservationService reservationService;
    @Mock
    private AppointmentFinalizationService finalizationService;
    @Mock
    private AppointmentCancellationService cancellationService;

    @InjectMocks
    private AppointmentService service;

    @Test
    public void reserve_success() {
        service.reserve(AppointmentDTO.builder().build());
        verify(reservationService, times(1)).reserve(eq(AppointmentDTO.builder().build()));
    }

    @Test
    public void finalize_success() {
        service.finalize("mockId");
        verify(finalizationService, times(1)).finalize(eq("mockId"));
    }

    @Test
    public void cancel_success() {
        service.cancel("mockId");
        verify(cancellationService, times(1)).cancel(eq("mockId"));
    }

    @Test
    public void retrieveAppointments_success() {

        AppointmentDTO expectedDTO = AppointmentDTO.builder().build();

        when(appointmentRepository.findAll()).thenReturn(
            Collections.singletonList(AppointmentEntity.builder().build()));

        when(appointmentMapper.map(any(AppointmentEntity.class))).thenReturn(expectedDTO);

        List<AppointmentDTO> appointmentDTOS = service.retrieveAppointments();

        assertEquals(appointmentDTOS.get(0), expectedDTO);

    }

    @Test
    public void retrieveAppointment_success() {

        AppointmentDTO expectedDTO = AppointmentDTO.builder().build();

        when(appointmentRepository.findById(eq("mockId"))).thenReturn(Optional.of(AppointmentEntity.builder().build()));

        when(appointmentMapper.map(any(AppointmentEntity.class))).thenReturn(expectedDTO);

        Optional<AppointmentDTO> appointmentDTOS = service.retrieve("mockId");

        assertEquals(appointmentDTOS.get(), expectedDTO);

    }

    @Test
    public void searchAppointments_success() {

        AppointmentDTO expectedDTO = AppointmentDTO.builder().build();

        when(appointmentRepository.findAll((Example<AppointmentEntity>) any())).thenReturn(
            Collections.singletonList(AppointmentEntity.builder().build()));

        when(appointmentMapper.map(any(AppointmentSearchRequest.class)))
            .thenReturn(AppointmentEntity.builder().build());

        when(appointmentMapper.map(any(AppointmentEntity.class)))
            .thenReturn(expectedDTO);

        List<AppointmentDTO> appointmentDTOS = service.searchAppointments(AppointmentSearchRequest.builder().build());

        assertEquals(appointmentDTOS.get(0), expectedDTO);

    }

}