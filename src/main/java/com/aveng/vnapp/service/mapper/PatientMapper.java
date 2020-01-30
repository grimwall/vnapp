package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.PatientEntity;
import com.aveng.vnapp.service.dto.PatientDTO;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientEntity map(PatientDTO patientDTO);

    PatientDTO map(PatientEntity patientEntity);
}
