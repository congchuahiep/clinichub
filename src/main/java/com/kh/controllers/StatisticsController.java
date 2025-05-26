package com.kh.controllers;

import com.kh.services.AppointmentService;
import com.kh.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;
    /**
     * Hiển thị thống kê số bệnh nhân đã khám
     * @param year Năm bắt buộc
     * @param month Tháng (1-12), không bắt buộc
     * @param quarter Quý (1-4), không bắt buộc
     */
    @GetMapping("/statistics/patients")
    public String getPatientStatistics(
            @RequestParam(name = "year", defaultValue = "2025") int year,
            @RequestParam(name = "month", required = false) Integer month,
            @RequestParam(name = "quarter", required = false) Integer quarter,
            Model model) {

        Long countPatients;

        if (month != null) {
            // Thống kê theo tháng
            countPatients = appointmentService.countPatientsCompletedByMonth(year, month);
        } else if (quarter != null) {
            // Thống kê theo quý
            countPatients = appointmentService.countPatientsCompletedByQuarter(year, quarter);
        } else {
            // Thống kê tổng quát không lọc theo tháng/quý
            countPatients = appointmentService.countPatientsCompleted();
        }

        model.addAttribute("countPatients", countPatients);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("quarter", quarter);

        return "statistics/patient-stats";  // view Thymeleaf hiển thị thống kê
    }

    @GetMapping("/statistics/patients/monthly")
    @ResponseBody
    public List<Long> getMonthlyStatistics(@RequestParam(name = "year", defaultValue = "2025") int year) {
        System.out.println("API được gọi với year: " + year);

        List<Long> monthlyStatistics = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Long count = appointmentService.countPatientsCompletedByMonth(year, month);
            System.out.println("Tháng " + month + ": " + count);
            monthlyStatistics.add(count != null ? count : 0L);
        }
        return monthlyStatistics;
    }

    @GetMapping("/statistics/revenue")
    public String getRevenueByMonth(
            @RequestParam(name = "year", defaultValue = "2025") int year,
            @RequestParam(name = "month", defaultValue = "5") int month,
            Model model) {

        BigDecimal revenue = paymentService.getRevenueByMonth(year, month);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("revenue", revenue);

        return "statistics/revenue-stats";
    }

    @GetMapping("/statistics/revenue/monthly")
    @ResponseBody
    public List<BigDecimal> getMonthlyRevenue(@RequestParam(name = "year", defaultValue = "2025") int year) {
        // Gọi service lấy doanh thu từng tháng dưới dạng Map<Integer, BigDecimal>
        Map<Integer, BigDecimal> revenueMap = paymentService.getRevenueByYear(year);

        // Chuyển map thành list 12 phần tử theo tháng (1..12)
        List<BigDecimal> monthlyRevenue = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            monthlyRevenue.add(revenueMap.getOrDefault(m, BigDecimal.ZERO));
        }

        return monthlyRevenue;
    }
}
