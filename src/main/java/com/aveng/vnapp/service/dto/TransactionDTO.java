package com.aveng.vnapp.service.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.aveng.vnapp.service.dto.enums.TransactionType;
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
public class TransactionDTO {

    private String id;

    private String appointmentId;

    private String patientId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.OFFSET_DATE_TIME_FORMAT)
    private OffsetDateTime creationDate;

    private TransactionType type;

    private BigDecimal cost;
}
