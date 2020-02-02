package com.aveng.vnapp.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.AppointmentSearchRequest;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

/**
 * @author apaydin
 */
@Service
public class AppointmentService {

    public static final ExampleMatcher APPOINTMENT_MATCHER =
        ExampleMatcher.matching().withIgnorePaths("duration, transactions");

    private AppointmentMapper appointmentMapper;
    private AppointmentRepository appointmentRepository;
    private TransactionService transactionService;
    private AppointmentReservationService reservationService;
    private AppointmentFinalizationService finalizationService;
    private AppointmentCancellationService cancellationService;


    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository,
        TransactionService transactionService, AppointmentReservationService reservationService,
        AppointmentFinalizationService finalizationService, AppointmentCancellationService cancellationService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.transactionService = transactionService;
        this.reservationService = reservationService;
        this.finalizationService = finalizationService;
        this.cancellationService = cancellationService;
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> retrieveAppointments() {

        return appointmentRepository.findAll()
            .parallelStream()
            .map(entity -> appointmentMapper.map(entity))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> searchAppointments(AppointmentSearchRequest searchRequest) {

        Example<AppointmentEntity> example = Example.of(appointmentMapper.map(searchRequest), APPOINTMENT_MATCHER);

        return appointmentRepository.findAll(example)
            .parallelStream()
            .map(entity -> appointmentMapper.map(entity))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<AppointmentDTO> retrieve(String id) {
        return appointmentRepository.findById(id)
            .map(entity -> appointmentMapper.map(entity))
            .map(this::addTransactionIds);
    }

    public AppointmentDTO reserve(AppointmentDTO requestDTO) {
        return reservationService.reserve(requestDTO);
    }

    public AppointmentDTO finalize(String appointmentId) {
        return finalizationService.finalize(appointmentId);
    }

    public AppointmentDTO cancel(String appointmentId) {
        return cancellationService.cancel(appointmentId);
    }

    private AppointmentDTO addTransactionIds(AppointmentDTO appointmentDTO) {

        appointmentDTO.setTransactions(transactionService.retrieveTransactionsByAppointmentId(appointmentDTO.getId())
            .stream()
            .map(TransactionDTO::getId)
            .collect(Collectors.toList()));
        return appointmentDTO;
    }
}
