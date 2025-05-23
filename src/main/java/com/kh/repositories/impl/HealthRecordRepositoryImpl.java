package com.kh.repositories.impl;

import com.kh.pojo.HealthRecord;
import com.kh.pojo.User;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.HealthRecordRepository;

import jakarta.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class HealthRecordRepositoryImpl extends AbstractRepository<HealthRecord, User> implements HealthRecordRepository {

    public HealthRecordRepositoryImpl() {
        super(HealthRecord.class);
    }
}
