package com.aveng.vnapp.service;

import static com.aveng.vnapp.service.exception.ApplicationException.getValidationException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.dto.enums.AppointmentState;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

/**
 * @author apaydin
 */
@Service
public class AppointmentFinalizationService {

    private AppointmentMapper appointmentMapper;
    private AppointmentRepository appointmentRepository;
    private DoctorService doctorService;
    private TransactionService transactionService;

    public AppointmentFinalizationService(AppointmentMapper appointmentMapper,
        AppointmentRepository appointmentRepository, DoctorService doctorService,
        TransactionService transactionService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.transactionService = transactionService;
    }

    @Transactional
    public AppointmentDTO finalize(String appointmentId) {

        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> getValidationException("Cannot find the required appointment, finalization aborted"));

        validateForFinalization(appointmentEntity);

        DoctorDTO doctorDTO = doctorService.retrieveDoctor(appointmentEntity.getDoctorId())
            .orElseThrow(() -> getValidationException("Cannot find the required doctor"));

        BigDecimal totalCost = calculateTotalCost(appointmentEntity, doctorDTO.getHourlyRate());

        TransactionEntity finalizeTransaction =
            transactionService.createFinalizeTransaction(appointmentId, appointmentEntity.getPatientId(), totalCost);

        appointmentEntity.setState(AppointmentState.FINALIZED);

        AppointmentDTO finalized = appointmentMapper.map(appointmentRepository.save(appointmentEntity));

        finalized.setTransactions(Collections.singletonList(finalizeTransaction.getId()));

        return finalized;
    }

    private void validateForFinalization(AppointmentEntity appointmentEntity) {
        if (appointmentEntity.getState() != AppointmentState.RESERVED) {
            throw ApplicationException.getValidationException("Appointment must be in RESERVED status!");
        }
    }

    private BigDecimal calculateTotalCost(AppointmentEntity appointmentEntity, BigDecimal hourlyRate) {
        return hourlyRate.multiply(BigDecimal.valueOf(
            appointmentEntity.getStartDate().until(appointmentEntity.getEndDate(), ChronoUnit.MINUTES)));
    }
}
