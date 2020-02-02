package com.aveng.vnapp.service;

import static com.aveng.vnapp.service.dto.enums.AppointmentState.RESERVED;
import static com.aveng.vnapp.service.exception.ApplicationException.getValidationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.dto.PatientDTO;
import com.aveng.vnapp.service.mapper.AppointmentMapper;
import com.aveng.vnapp.service.util.DateUtil;

/**
 * @author apaydin
 */
@Service
public class AppointmentReservationService {

    private AppointmentMapper appointmentMapper;
    private AppointmentRepository appointmentRepository;
    private DoctorService doctorService;
    private PatientService patientService;

    public AppointmentReservationService(AppointmentMapper appointmentMapper,
        AppointmentRepository appointmentRepository, DoctorService doctorService, PatientService patientService) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    @Transactional
    public AppointmentDTO reserve(AppointmentDTO requestDTO) {

        //check if patient and doctor exists
        DoctorDTO doctorDTO = doctorService.retrieveDoctor(requestDTO.getDoctorId())
            .orElseThrow(() -> getValidationException("Cannot find the required doctor"));

        PatientDTO patientDTO = patientService.retrievePatient(requestDTO.getPatientId())
            .orElseThrow(() -> getValidationException("Cannot find the required patient"));

        //calculate end date and check if the doctor is free at that time
        Instant startDate = DateUtil.convertOffsetDateTimeToUTCInstant(requestDTO.getStartDate());
        Instant endDate = startDate.plus(requestDTO.getDuration(), ChronoUnit.MINUTES);

        validateFreeAppointmentSlot(doctorDTO.getId(), startDate, endDate);

        return reserve(doctorDTO.getId(), patientDTO.getId(), startDate, endDate);
    }

    private void validateFreeAppointmentSlot(String doctorId, Instant startDate, Instant endDate) {

        List<AppointmentEntity> conflictingEvents =
            appointmentRepository.findAllConflictingValidEvents(startDate, endDate, doctorId);

        if (!conflictingEvents.isEmpty()) {
            throw getValidationException("Appointment conflicts with previous appointments");
        }
    }

    private AppointmentDTO reserve(String doctorId, String patientId, Instant startDate, Instant endDate) {

        AppointmentEntity appointmentEntity = appointmentRepository.save(AppointmentEntity.builder()
            .doctorId(doctorId)
            .patientId(patientId)
            .startDate((startDate))
            .endDate((endDate))
            .state(RESERVED)
            .build());

        return appointmentMapper.map(appointmentEntity);
    }
}
