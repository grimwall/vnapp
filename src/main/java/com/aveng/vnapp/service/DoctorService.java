package com.aveng.vnapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aveng.vnapp.domain.Doctor;
import com.aveng.vnapp.repository.DoctorRepository;
import com.aveng.vnapp.service.dto.DoctorDTO;
import com.aveng.vnapp.service.mapper.DoctorMapper;

/**
 * @author apaydin
 */
@Service
public class DoctorService {

    private DoctorMapper doctorMapper;
    private DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorMapper doctorMapper, DoctorRepository doctorRepository) {
        this.doctorMapper = doctorMapper;
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorDTO> retrieveDoctors() {
        return doctorRepository.findAll()
            .stream()
            .map(doctor -> doctorMapper.map(doctor))
            .collect(Collectors.toList());
    }

    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = doctorMapper.map(doctorDTO);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.map(savedDoctor);
    }
}
