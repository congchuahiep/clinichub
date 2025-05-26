package com.kh.services.impl;

import com.kh.repositories.PaymentRepository;
import com.kh.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public BigDecimal getRevenueByMonth(int year, int month) {
        BigDecimal revenue = paymentRepository.sumAmountByMonth(year, month);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public Map<Integer, BigDecimal> getRevenueByYear(int year) {
        List<Object[]> results = paymentRepository.sumAmountGroupByMonth(year);
        Map<Integer, BigDecimal> revenueByMonth = new HashMap<>();
        for (int m = 1; m <= 12; m++) revenueByMonth.put(m, BigDecimal.ZERO);

        for (Object[] row : results) {
            Integer month = (Integer) row[0];
            BigDecimal total = (BigDecimal) row[1];
            revenueByMonth.put(month, total);
        }
        return revenueByMonth;
    }

}
