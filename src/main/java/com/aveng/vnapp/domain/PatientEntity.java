package com.aveng.vnapp.domain;

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
@Table(name = "PATIENT", schema = "VENGEANCE")
public class PatientEntity extends BaseEntity {

    private static final long serialVersionUID = 12433232L;

    @Size(max = 250)
    @Column(name = "FIRST_NAME", length = 250)
    private String firstName;

    @Size(max = 250)
    @Column(name = "LAST_NAME", length = 250)
    private String lastName;
}
