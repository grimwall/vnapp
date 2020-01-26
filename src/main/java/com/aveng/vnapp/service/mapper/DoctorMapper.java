package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.DoctorEntity;
import com.aveng.vnapp.service.dto.DoctorDTO;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorEntity map(DoctorDTO doctorDTO);

    DoctorDTO map(DoctorEntity doctorEntity);
}
