package com.aveng.vnapp.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aveng.vnapp.service.AppointmentService;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.AppointmentSearchRequest;

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

    @PostMapping("/search")
    public ResponseEntity<List<AppointmentDTO>> retrieveAppointments(
        @Valid @RequestBody AppointmentSearchRequest request) {

        List<AppointmentDTO> appointmentS = appointmentService.searchAppointments(request);

        //todo pagination

        return new ResponseEntity<>(appointmentS, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> reserve(@Valid @RequestBody AppointmentDTO appointmentDTO) {

        log.info(String.format("got a new appointment request: %s", appointmentDTO));

        AppointmentDTO reserveAppointment = appointmentService.reserve(appointmentDTO);

        return new ResponseEntity<>(reserveAppointment, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/finalize")
    public ResponseEntity<AppointmentDTO> finalize(@PathVariable String id) {

        log.info(String.format("got a new appointment finalize request: %s", id));

        AppointmentDTO finalizedAppointment = appointmentService.finalize(id);

        return new ResponseEntity<>(finalizedAppointment, HttpStatus.OK);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancel(@PathVariable String id) {

        AppointmentDTO cancelledAppointment = appointmentService.cancel(id);

        return new ResponseEntity<>(cancelledAppointment, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> retrieve(@PathVariable String id) {

        return appointmentService.retrieve(id)
            .map(appointmentDTO -> new ResponseEntity<>(appointmentDTO, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
