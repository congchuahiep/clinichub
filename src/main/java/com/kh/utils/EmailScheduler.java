package com.kh.utils;

import com.kh.dtos.EmailDTO;
import com.kh.pojo.Appointment;
import com.kh.repositories.AppointmentRepository;
import com.kh.services.EmailService;
import com.kh.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class EmailScheduler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Hàm chạy tự động mỗi ngày vào lúc 00:00 để kiểm tra và gửi email nhắc nhở trước 24 giờ.
     */
    @Scheduled(cron = "0 40 20 * * *") // Mỗi ngày vào lúc 00:00
    public void sendReminderEmails() {
        // Lấy thời điểm hiện tại và thời điểm 24 giờ sau
        Date now = new Date();
        Date next24Hours = new Date(now.getTime() + (24 * 60 * 60 * 1000));

        // Lấy tất cả các lịch hẹn trong khoảng 24 giờ tiếp theo
        List<Appointment> appointments = appointmentRepository.findAppointmentsBetweenDates(now, next24Hours);

        // Gửi email nhắc nhở cho từng bệnh nhân có lịch hẹn
        for (Appointment appointment : appointments) {
            sendReminderToPatient(appointment);
        }
    }

    /**
     * Gửi email nhắc nhở tới bệnh nhân.
     */
    private void sendReminderToPatient(Appointment appointment) {
        String formattedDate = DateUtils.formatVietnameseDate(appointment.getAppointmentDate());

        EmailDTO email = new EmailDTO();
        email.setToEmail(appointment.getPatientId().getEmail());
        email.setSubject("Nhắc nhở lịch khám bác sĩ vào ngày mai");

        String body = String.format(
                "Kính chào %s %s,\n\n" +
                        "Hệ thống nhắc nhở bạn rằng bạn có lịch khám với bác sĩ %s %s vào ngày mai.\n\n" +
                        "Chi tiết lịch hẹn:\n" +
                        "🗓 Ngày khám: %s\n" +
                        "⏰ Ca khám: %s\n" +
                        "📝 Ghi chú: %s\n\n" +
                        "Vui lòng đến sớm 15 phút để làm thủ tục trước khi khám.\n\n" +
                        "Trân trọng,\nPhòng khám ABC",
                appointment.getPatientId().getLastName(),
                appointment.getPatientId().getFirstName(),
                appointment.getDoctorId().getLastName(),
                appointment.getDoctorId().getFirstName(),
                formattedDate,
                appointment.getTimeSlot().getSlotNumber(),
                appointment.getNote() == null || appointment.getNote().isEmpty() ? "Không có" : appointment.getNote()
        );

        email.setBody(body);
        emailService.sendEmail(email);
    }
}
