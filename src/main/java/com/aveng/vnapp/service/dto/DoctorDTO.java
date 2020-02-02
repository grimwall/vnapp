package com.aveng.vnapp.service.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author apaydin
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {

    private String id;

    @NotBlank
    @Size(max = 250)
    private String firstName;

    @NotBlank
    @Size(max = 250)
    private String lastName;

    @NotNull
    @Min(0)
    private BigDecimal hourlyRate;
}
