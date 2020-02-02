package com.aveng.vnapp.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.aveng.vnapp.domain.enums.AppointmentState;

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
@Table(name = "APPOINTMENT", schema = "VENGEANCE")
public class AppointmentEntity extends BaseEntity {

    private static final long serialVersionUID = 124154543132L;

    @Size(max = 36)
    @Column(name = "DOCTOR_ID", length = 36)
    private String doctorId;

    @Size(max = 36)
    @Column(name = "PATIENT_ID", length = 36)
    private String patientId;

    @Column(name = "START_DATE")
    private Instant startDate;

    @Column(name = "END_DATE")
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE")
    private AppointmentState state;
}
