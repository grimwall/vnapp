package com.aveng.vnapp.service;

import static com.aveng.vnapp.domain.enums.AppointmentState.FINALIZED;
import static com.aveng.vnapp.service.exception.ApplicationException.getValidationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.domain.enums.AppointmentState;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

/**
 * Handles all logic for Appointment cancellation
 *
 * @author apaydin
 */
@Service
public class AppointmentCancellationService {

    public static final int LATE_CANCEL_THRESHOLD_MINUTES = 60;
    public static final BigDecimal CANCEL_PENALTY_PERCENTAGE = BigDecimal.valueOf(0.25);

    private AppointmentMapper appointmentMapper;
    private AppointmentRepository appointmentRepository;
    private TransactionService transactionService;

    public AppointmentCancellationService(AppointmentMapper appointmentMapper,
        AppointmentRepository appointmentRepository, TransactionService transactionService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.transactionService = transactionService;
    }

    /**
     * Cancels the required appointment and if eligible for cancellation fee, creates a transaction with cancelled status.
     *
     * @param appointmentId The id of the appointment to be cancelled
     * @return the updated Appointment
     */
    @Transactional
    public AppointmentDTO cancel(String appointmentId) {

        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> getValidationException("Cannot find the required appointment, cancel aborted"));

        if (FINALIZED != appointmentEntity.getState()) {
            throw getValidationException("You can only cancel FINALIZED appointments!");
        }

        handleCancellationTransaction(appointmentEntity);

        appointmentEntity.setState(AppointmentState.CANCELLED);

        AppointmentDTO cancelled = appointmentMapper.map(appointmentRepository.save(appointmentEntity));

        addTransactionIds(cancelled);

        return cancelled;
    }

    private void handleCancellationTransaction(AppointmentEntity appointmentEntity) {

        if (isEligibleForLateCancelFee(appointmentEntity)) {

            List<TransactionDTO> transactionDTOS =
                transactionService.retrieveTransactionsByAppointmentId(appointmentEntity.getId());

            if (CollectionUtils.isEmpty(transactionDTOS)) {
                throw new ApplicationException("Cannot find the original transaction for cancel flow!");
            }

            TransactionDTO finalization = transactionDTOS.get(0);

            BigDecimal cancelFee =
                finalization.getCost().multiply(CANCEL_PENALTY_PERCENTAGE).setScale(2, BigDecimal.ROUND_HALF_UP);

            transactionService.createCancelTransaction(appointmentEntity.getId(), appointmentEntity.getPatientId(),
                appointmentEntity.getStartDate(), cancelFee);
        }
    }

    private boolean isEligibleForLateCancelFee(AppointmentEntity appointmentEntity) {
        return Instant.now().until(appointmentEntity.getStartDate(), ChronoUnit.MINUTES)
            <= LATE_CANCEL_THRESHOLD_MINUTES;
    }

    private void addTransactionIds(AppointmentDTO appointmentDTO) {

        appointmentDTO.setTransactions(transactionService.retrieveTransactionsByAppointmentId(appointmentDTO.getId())
            .stream()
            .map(TransactionDTO::getId)
            .collect(Collectors.toList()));
    }
}
