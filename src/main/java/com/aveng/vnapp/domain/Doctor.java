package com.aveng.vnapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author apaydin
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DOCTOR", schema = "VENGEANCE")
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", columnDefinition = "varchar(36)", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Size(max = 250)
    @Column(name = "FIRST_NAME", length = 250)
    private String firstName;

    @Size(max = 250)
    @Column(name = "LAST_NAME", length = 250)
    private String lastName;

    @Column(name = "HOURLY_RATE")
    private BigDecimal hourlyRate;
}
