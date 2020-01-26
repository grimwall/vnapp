package com.aveng.vnapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aveng.vnapp.domain.Doctor;

/**
 * @author apaydin
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
