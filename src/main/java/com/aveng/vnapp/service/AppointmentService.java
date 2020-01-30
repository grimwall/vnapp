package com.aveng.vnapp.service;

import static com.aveng.vnapp.service.dto.enums.AppointmentState.RESERVED;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.dto.PatientDTO;
import com.aveng.vnapp.service.dto.TransactionDTO;
import com.aveng.vnapp.service.dto.enums.AppointmentState;
import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

/**
 * @author apaydin
 */
@Service
public class AppointmentService {

    public static final int LATE_CANCEL_THRESHOLD_MINUTES = 60;
    public static final BigDecimal CANCEL_PENALTY_PERCENTAGE = BigDecimal.valueOf(0.25);

    private AppointmentMapper appointmentMapper;
    private AppointmentRepository appointmentRepository;
    private DoctorService doctorService;
    private PatientService patientService;
    private TransactionService transactionService;

    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository,
        DoctorService doctorService, PatientService patientService, TransactionService transactionService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.transactionService = transactionService;
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> retrieveAppointments() {

        return appointmentRepository.findAll()
            .stream()
            .map(entity -> appointmentMapper.map(entity))
            .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTO reserveAppointment(AppointmentDTO requestDTO) {

        //check if patient and doctor exists
        DoctorDTO doctorDTO = doctorService.retrieveDoctor(requestDTO.getDoctorId())
            .orElseThrow(() -> getValidationException("Cannot find the required doctor"));

        PatientDTO patientDTO = patientService.retrievePatient(requestDTO.getPatientId())
            .orElseThrow(() -> getValidationException("Cannot find the required patient"));

        //calculate end date and check if the doctor is free at that time
        OffsetDateTime startDate = requestDTO.getStartDate();
        OffsetDateTime endDate = startDate.plusMinutes(requestDTO.getDuration());

        validateFreeAppointmentSlot(doctorDTO.getId(), startDate, endDate);

        return reserve(doctorDTO.getId(), patientDTO.getId(), startDate, endDate);
    }

    private AppointmentDTO reserve(String doctorId, String patientId, OffsetDateTime startDate,
        OffsetDateTime endDate) {

        AppointmentEntity appointmentEntity = appointmentRepository.save(AppointmentEntity.builder()
            .doctorId(doctorId)
            .patientId(patientId)
            .startDate(startDate)
            .endDate(endDate)
            .state(RESERVED)
            .build());

        return appointmentMapper.map(appointmentEntity);
    }

    @Transactional
    public AppointmentDTO finalize(String appointmentId) {

        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> getValidationException("Cannot find the required appointment, finalization aborted"));

        DoctorDTO doctorDTO = doctorService.retrieveDoctor(appointmentEntity.getDoctorId())
            .orElseThrow(() -> getValidationException("Cannot find the required doctor"));

        BigDecimal totalCost = calculateTotalCost(appointmentEntity, doctorDTO.getHourlyRate());

        transactionService.createFinalizeTransaction(appointmentId, appointmentEntity.getPatientId(), totalCost);

        appointmentEntity.setState(AppointmentState.FINALIZED);
        return appointmentMapper.map(appointmentRepository.save(appointmentEntity));
    }

    @Transactional
    public AppointmentDTO cancel(String appointmentId) {

        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> getValidationException("Cannot find the required appointment, cancel aborted"));

        if (RESERVED != appointmentEntity.getState()) {
            throw getValidationException("You can only cancel RESERVED appointments!");
        }

        handleCancellationTransaction(appointmentEntity);

        appointmentEntity.setState(AppointmentState.CANCELLED);
        return appointmentMapper.map(appointmentRepository.save(appointmentEntity));
    }

    private void handleCancellationTransaction(AppointmentEntity appointmentEntity) {

        if (isEligibleForLateCancelFee(appointmentEntity)) {

            List<TransactionDTO> transactionDTOS =
                transactionService.retrieveTransactionsByAppointmentId(appointmentEntity.getId());

            if (CollectionUtils.isEmpty(transactionDTOS)) {
                throw new ApplicationException("Cannot find the original transaction for cancel flow!");
            }

            TransactionDTO reservationTransaction = transactionDTOS.get(0);

            BigDecimal cancelFee = reservationTransaction.getCost().multiply(CANCEL_PENALTY_PERCENTAGE);

            transactionService.createCancelTransaction(appointmentEntity.getId(), appointmentEntity.getPatientId(),
                appointmentEntity.getStartDate(), cancelFee);
        }
    }

    private boolean isEligibleForLateCancelFee(AppointmentEntity appointmentEntity) {
        return OffsetDateTime.now().until(appointmentEntity.getEndDate(), ChronoUnit.MINUTES)
            <= LATE_CANCEL_THRESHOLD_MINUTES;
    }

    private BigDecimal calculateTotalCost(AppointmentEntity appointmentEntity, BigDecimal hourlyRate) {
        return hourlyRate.multiply(BigDecimal.valueOf(
            appointmentEntity.getStartDate().until(appointmentEntity.getEndDate(), ChronoUnit.MINUTES)));
    }

    private void validateFreeAppointmentSlot(String doctorId, OffsetDateTime startDate, OffsetDateTime endDate) {

        List<AppointmentEntity> conflictingEvents =
            appointmentRepository.findAllConflictingValidEvents(startDate, endDate, doctorId);

        if (!conflictingEvents.isEmpty()) {
            throw getValidationException("Appointment conflicts with previous appointments");
        }
    }

    private ApplicationException getValidationException(String s) {
        return new ApplicationException(HttpStatus.BAD_REQUEST, s, Level.INFO);
    }
}
