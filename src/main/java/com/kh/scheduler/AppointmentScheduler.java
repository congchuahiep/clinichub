package com.kh.scheduler;

import com.kh.repositories.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppointmentScheduler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentScheduler.class);

    /**
     * Chạy mỗi giờ để kiểm tra và cập nhật trạng thái lịch hẹn đã quá hạn
     */
    @Scheduled(cron = "0 0 * * * *")
    public void updateExpiredAppointments() {
        try {
            logger.info("Bắt đầu cập nhật trạng thái lịch hẹn quá hạn...");
            appointmentRepository.updateExpiredAppointments();
            logger.info("Hoàn thành cập nhật trạng thái lịch hẹn quá hạn");
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật trạng thái lịch hẹn: {}", e.getMessage(), e);
        }
    }
}