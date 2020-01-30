package com.aveng.vnapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aveng.vnapp.domain.AppointmentEntity;

/**
 * @author apaydin
 */
@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {

}
