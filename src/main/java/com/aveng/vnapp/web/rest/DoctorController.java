package com.aveng.vnapp.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aveng.vnapp.service.DoctorService;
import com.aveng.vnapp.service.dto.DoctorDTO;

/**
 * @author apaydin
 */
@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getDoctors() {

        List<DoctorDTO> doctors = doctorService.retrieveDoctors();

        //todo pagination

        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        DoctorDTO doctor = doctorService.createDoctor(doctorDTO);

        return new ResponseEntity<>(doctor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@Valid @RequestBody DoctorDTO doctorDTO, @PathVariable String id) {
        DoctorDTO doctor = doctorService.updateDoctor(doctorDTO, id);

        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }
}
