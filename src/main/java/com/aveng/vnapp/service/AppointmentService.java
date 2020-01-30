package com.aveng.vnapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.repository.AppointmentRepository;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.mapper.AppointmentMapper;

/**
 * @author apaydin
 */
@Service
public class AppointmentService {

    private AppointmentMapper appointmentMapper;
    private AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentMapper appointmentMapper, AppointmentRepository appointmentRepository) {
        this.appointmentMapper = appointmentMapper;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> retrieveAppointments() {

        return appointmentRepository.findAll()
            .stream()
            .map(entity -> appointmentMapper.map(entity))
            .collect(Collectors.toList());
    }
}
