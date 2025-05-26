package com.kh.services;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    BigDecimal getRevenueByMonth(int year, int month);

    Map<Integer, BigDecimal> getRevenueByYear(int year);

}
