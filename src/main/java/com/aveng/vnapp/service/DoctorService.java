package com.aveng.vnapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.domain.DoctorEntity;
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

    @Transactional(readOnly = true)
    public List<DoctorDTO> retrieveDoctors() {
        return doctorRepository.findAll()
            .stream()
            .map(doctorEntity -> doctorMapper.map(doctorEntity))
            .collect(Collectors.toList());
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        DoctorEntity doctorEntity = doctorMapper.map(doctorDTO);

        DoctorEntity savedDoctorEntity = doctorRepository.save(doctorEntity);

        return doctorMapper.map(savedDoctorEntity);
    }
}
