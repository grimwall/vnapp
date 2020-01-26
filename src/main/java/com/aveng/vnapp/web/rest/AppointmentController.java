package com.aveng.vnapp.web.rest;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aveng.vnapp.service.dto.AppointmentDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author apaydin
 */
@RestController
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        log.info(String.format("got a new appointment request: %s", appointmentDTO));
        return new ResponseEntity<>(appointmentDTO, HttpStatus.CREATED);
    }
}
