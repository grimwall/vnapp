package com.aveng.vnapp.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author apaydin
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DOCTOR", schema = "VENGEANCE")
public class DoctorEntity extends BaseEntity {

    private static final long serialVersionUID = 12432L;

    @Size(max = 250)
    @Column(name = "FIRST_NAME", length = 250)
    private String firstName;

    @Size(max = 250)
    @Column(name = "LAST_NAME", length = 250)
    private String lastName;

    @Column(name = "HOURLY_RATE", precision = 17, scale = 2)
    private BigDecimal hourlyRate;
}
