package com.aveng.vnapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aveng.vnapp.domain.TransactionEntity;

/**
 * @author apaydin
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

}
