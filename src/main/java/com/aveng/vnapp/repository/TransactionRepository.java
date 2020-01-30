package com.aveng.vnapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aveng.vnapp.domain.TransactionEntity;

/**
 * @author apaydin
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findAllByAppointmentId(String appointmentId);
}
