package com.kh.repositories.impl;

import com.kh.pojo.Appointment;
import com.kh.pojo.Payment;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.PaymentRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class PaymentRepositoryImpl extends AbstractRepository<Payment, BigDecimal> implements PaymentRepository {

    public PaymentRepositoryImpl() {
        super(Payment.class);
    }

    public BigDecimal sumAmountByMonth(int year, int month) {
        Session session = getCurrentSession();

        String hql = "SELECT COALESCE(SUM(p.amount), 0) " +
                "FROM Payment p " +
                "WHERE p.paymentStatus = :status " +
                "AND YEAR(p.paymentDate) = :year " +
                "AND MONTH(p.paymentDate) = :month";

        Query<BigDecimal> query = session.createQuery(hql, BigDecimal.class);
        query.setParameter("status", "completed");
        query.setParameter("year", year);
        query.setParameter("month", month);

        return query.uniqueResultOptional().orElse(BigDecimal.ZERO);
    }

    @Override
    public List<Object[]> sumAmountGroupByMonth(int year) {
        Session session = getCurrentSession();
        String hql = "SELECT MONTH(p.paymentDate), COALESCE(SUM(p.amount), 0) " +
                "FROM Payment p " +
                "WHERE p.paymentStatus = :status AND YEAR(p.paymentDate) = :year " +
                "GROUP BY MONTH(p.paymentDate) " +
                "ORDER BY MONTH(p.paymentDate)";
        Query<Object[]> query = session.createQuery(hql, Object[].class);
        query.setParameter("status", "completed");
        query.setParameter("year", year);
        return query.getResultList();
    }


}
