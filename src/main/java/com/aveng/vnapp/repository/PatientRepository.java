package com.aveng.vnapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aveng.vnapp.domain.PatientEntity;

/**
 * @author apaydin
 */
@Repository
public interface PatientRepository extends JpaRepository<PatientEntity, String> {

}
