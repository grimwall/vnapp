package com.aveng.vnapp.service.mapper;

import org.mapstruct.Mapper;

import com.aveng.vnapp.domain.AppointmentEntity;
import com.aveng.vnapp.service.dto.AppointmentDTO;
import com.aveng.vnapp.service.dto.AppointmentSearchRequest;
import com.aveng.vnapp.service.util.DateUtil;

/**
 * @author apaydin
 */
@Mapper(componentModel = "spring", uses = DateUtil.class)
public interface AppointmentMapper {

    AppointmentEntity map(AppointmentDTO appointmentDTO);

    AppointmentDTO map(AppointmentEntity appointmentEntity);

    AppointmentEntity map(AppointmentSearchRequest request);
}
