package com.aveng.vnapp.domain;

import java.math.BigDecimal;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.aveng.vnapp.domain.enums.TransactionType;

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
@Table(name = "TRANSACTION", schema = "VENGEANCE")
public class TransactionEntity extends BaseEntity {

    private static final long serialVersionUID = 15452432L;

    @Size(max = 36)
    @Column(name = "APPOINTMENT_ID", length = 36)
    private String appointmentId;

    @Size(max = 36)
    @Column(name = "PATIENT_ID", length = 36)
    private String patientId;

    @Column(name = "TRANSACTION_DATE")
    private Instant transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private TransactionType type;

    @Column(name = "COST")
    private BigDecimal cost;
}