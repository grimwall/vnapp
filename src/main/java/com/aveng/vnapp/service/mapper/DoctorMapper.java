package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.Doctor;
import com.aveng.vnapp.service.dto.DoctorDTO;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper {

    Doctor map(DoctorDTO doctorDTO);

    DoctorDTO map(Doctor doctor);
}
