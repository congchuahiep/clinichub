package com.kh.repositories;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository {
    BigDecimal sumAmountByMonth(int year, int month);

    List<Object[]> sumAmountGroupByMonth(int year);

}
