package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.service.dto.AppointmentDTO;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentEntity map(AppointmentDTO appointmentDTO);

    AppointmentDTO map(AppointmentEntity appointmentEntity);
}
