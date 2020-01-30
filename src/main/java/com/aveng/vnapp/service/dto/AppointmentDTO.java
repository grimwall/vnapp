package com.aveng.vnapp.service.dto;

import static com.aveng.vnapp.domain.DomainConstants.UUID_SIZE;

import java.time.OffsetDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.aveng.vnapp.service.dto.enums.AppointmentState;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author apaydin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private String id;

    @NotBlank
    @Size(max = UUID_SIZE, min = UUID_SIZE)
    private String doctorId;

    @NotBlank
    @Size(max = UUID_SIZE, min = UUID_SIZE)
    private String patientId;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.OFFSET_DATE_TIME_FORMAT)
    private OffsetDateTime startDate;

    //in minutes
    @Max(Constants.MAX_DURATION)
    private int duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.OFFSET_DATE_TIME_FORMAT)
    private OffsetDateTime endDate;

    private AppointmentState state;
}
