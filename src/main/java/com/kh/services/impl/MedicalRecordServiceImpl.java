package com.kh.services.impl;

import com.kh.dtos.MedicalRecordDTO;
import com.kh.pojo.*;
import com.kh.repositories.*;
import com.kh.services.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Override
    @Transactional
    public MedicalRecordDTO addMedicalRecord(MedicalRecordDTO medicalRecordDTO) {
        Appointment appointment = appointmentRepository.findById(medicalRecordDTO.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Lịch khám này không tồn tại!"));

        if (this.appointmentRepository.existsAppointmentMedicalRecord(appointment.getId())) {
            throw new RuntimeException("Lịch khám này đã được chẩn đoán rồi!");
        }

        User doctor = userRepository.findDoctorById(medicalRecordDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Bác sĩ không tồn tại!"));

        // Kiểm tra bác sĩ có khám lịch này hay không
        if (!this.appointmentRepository.existsAppointmentBetweenDoctorAndPatient(doctor.getId(), appointment.getPatientId().getId())) {
            throw new RuntimeException("Bác sĩ chỉ được xem hồ sơ của bệnh nhân đã có lịch hẹn");
        }

        HealthRecord healthRecord = healthRecordRepository.findById(appointment.getPatientId().getId())
                .orElseThrow(() -> new RuntimeException("Hồ sơ sức khoẻ này không tồn tại!"));

        Disease disease = medicalRecordDTO.getDiseaseId() != null
                ? diseaseRepository.findById(medicalRecordDTO.getDiseaseId())
                .orElseThrow(() -> new RuntimeException("Loại bệnh này không tồn tại!"))
                : null;

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointment(appointment);
        medicalRecord.setHealthRecordId(healthRecord);
        medicalRecord.setDoctorId(doctor);
        medicalRecord.setDiseaseId(disease);
        medicalRecord.setNotes(medicalRecordDTO.getNotes());
        medicalRecord.setDiagnosis(medicalRecordDTO.getDiagnosis());
        medicalRecord.setTestResults(medicalRecordDTO.getTestResults());
        medicalRecord.setPrescriptions(medicalRecordDTO.getPrescriptions());
        medicalRecord.setDiagnosisDate(new Date());

        appointment.setStatus("completed");

        medicalRecordRepository.save(medicalRecord);
        appointmentRepository.save(appointment);

        return new MedicalRecordDTO(medicalRecord);
    }
}