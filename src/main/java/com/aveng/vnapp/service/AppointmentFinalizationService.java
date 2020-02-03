package com.aveng.vnapp.service;

import static com.aveng.vnapp.service.exception.ApplicationException.getValidationException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.domain.TransactionEntity;
import com.aveng.vnapp.domain.enums.AppointmentState;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

/**
 * Handles all logic for Appointment finalization
 *
 * @author apaydin
 */
@Service
public class AppointmentFinalizationService {

    public static final BigDecimal MINUTES_IN_AN_HOUR = BigDecimal.valueOf(60);
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

    /**
     * Finalizes a given appointment that is in RESERVED status. Creates a transaction based on the hourly rate of the
     * associated doctor
     *
     * @param appointmentId Appointment id
     * @return Updated appointment
     */
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
        return BigDecimal.valueOf(
            appointmentEntity.getStartDate().until(appointmentEntity.getEndDate(), ChronoUnit.MINUTES))
            .divide(MINUTES_IN_AN_HOUR, 6, BigDecimal.ROUND_HALF_UP)
            .multiply(hourlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
