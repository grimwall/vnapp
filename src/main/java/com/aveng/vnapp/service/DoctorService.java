package com.aveng.vnapp.service;

import static com.aveng.vnapp.service.exception.ApplicationException.getValidationException;

import java.util.List;
import java.util.Optional;
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

    @Transactional(readOnly = true)
    public Optional<DoctorDTO> retrieveDoctor(String doctorId) {
        return Optional.ofNullable(doctorId)
            .flatMap(id -> doctorRepository.findById(id))
            .map(entity -> doctorMapper.map(entity));
    }

    @Transactional
    public DoctorDTO updateDoctor(DoctorDTO doctorDTO, String id) {

        String existingDoctorId = doctorRepository.findById(id)
            .map(DoctorEntity::getId)
            .orElseThrow(() -> getValidationException("Cannot find the required doctor"));

        DoctorDTO updateRequest = doctorDTO.toBuilder().id(existingDoctorId).build();

        DoctorEntity toBeSavedEntity = doctorMapper.map(updateRequest);
        toBeSavedEntity.setId(existingDoctorId);

        DoctorEntity updatedDoctor = doctorRepository.save(toBeSavedEntity);

        return doctorMapper.map(updatedDoctor);
    }
}
