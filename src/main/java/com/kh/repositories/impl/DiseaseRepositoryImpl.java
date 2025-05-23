package com.kh.repositories.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kh.pojo.Disease;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.DiseaseRepository;

@Repository
@Transactional
public class DiseaseRepositoryImpl extends AbstractRepository<Disease, Long> implements DiseaseRepository {

    public DiseaseRepositoryImpl() {
        super(Disease.class);
    }

}
