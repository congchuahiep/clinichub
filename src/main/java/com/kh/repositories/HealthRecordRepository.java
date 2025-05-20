package com.kh.repositories;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import com.kh.pojo.HealthRecord;
import java.util.List;
import java.util.Optional;
//extends JpaRepository<HealthRecord, Long>
public interface HealthRecordRepository {

    Optional<HealthRecord> findById(Long id);

    Optional<HealthRecord> findByPatientId(Long patientId);

    List<HealthRecord> findAll();

    HealthRecord save(HealthRecord healthRecord);

    void delete(Long id);

    boolean existsAppointmentBetweenDoctorAndPatient(Long doctorId, Long patientId);
    
//    @Query("SELECT h FROM HealthRecord h WHERE h.patientId.id = :patientId")
//    Optional<HealthRecord> findByPatientId(@Param("patientId") Long patientId);
}
    