/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kh.repositories.impl;

import com.kh.pojo.DoctorLicense;
import com.kh.repositories.AbstractRepository;
import com.kh.repositories.DoctorLisenceRepository;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public class DoctorLisenceRepositoryImpl extends AbstractRepository<DoctorLicense, Long> implements DoctorLisenceRepository {

    protected DoctorLisenceRepositoryImpl() {
        super(DoctorLicense.class);
    }
}
