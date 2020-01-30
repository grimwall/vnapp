package com.aveng.vnapp.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aveng.vnapp.service.AppointmentService;
import com.aveng.vnapp.service.dto.AppointmentDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author apaydin
 */
@RestController
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {

    private AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> retrieveAppointments() {

        List<AppointmentDTO> appointmentS = appointmentService.retrieveAppointments();

        //todo pagination

        return new ResponseEntity<>(appointmentS, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        log.info(String.format("got a new appointment request: %s", appointmentDTO));
        return new ResponseEntity<>(appointmentDTO, HttpStatus.CREATED);
    }
}
