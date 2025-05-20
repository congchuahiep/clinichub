package com.kh.repositories.impl;

import com.kh.pojo.Specialty;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.SpecialtyRepository;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class SpecialtyRepositoryImpl extends AbstractRepository implements SpecialtyRepository {

    public SpecialtyRepositoryImpl(LocalSessionFactoryBean factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Specialty> findById(Long id) {
        Session session = getCurrentSession();

        try {
            Specialty specialty = session.get(Specialty.class, id);
            return Optional.ofNullable(specialty);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
