package com.aveng.vnapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aveng.vnapp.repository.PatientRepository;
import com.aveng.vnapp.service.dto.PatientDTO;
import com.aveng.vnapp.service.mapper.PatientMapper;

/**
 * @author apaydin
 */
@Service
public class PatientService {

    private PatientMapper patientMapper;
    private PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientMapper patientMapper, PatientRepository patientRepository) {
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> retrievePatients() {

        return patientRepository.findAll()
            .stream()
            .map(entity -> patientMapper.map(entity))
            .collect(Collectors.toList());
    }
}
