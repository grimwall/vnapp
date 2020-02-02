package com.aveng.vnapp.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aveng.vnapp.domain.AppointmentEntity;

/**
 * @author apaydin
 */
@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, String> {

    @Query(value = "from AppointmentEntity t where :doctorId = t.doctorId "
        + "and t.state <> 'CANCELLED' "
        + "and ((:startDate between t.startDate and t.endDate) "
        + "or (:endDate between t.startDate and t.endDate) "
        + "or (:startDate <= t.startDate and :endDate >= t.endDate))")
    List<AppointmentEntity> findAllConflictingValidEvents(@Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate, @Param("doctorId") String doctorId);
}
