/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.kh.repositories;

import com.kh.dtos.DoctorWithRating;
import com.kh.exceptions.EmailAlreadyExistsException;
import com.kh.exceptions.UsernameAlreadyExistsException;
import com.kh.pojo.User;
import com.kh.utils.PaginatedResult;

import java.util.Map;
import java.util.Optional;

/**
 *
 */
public interface UserRepository extends GenericRepository<User, Long> {
    User save(User user) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, IllegalStateException;

    PaginatedResult<DoctorWithRating> doctorList(Map<String, String> params);

    Optional<User> findDoctorById(Long id);

    Optional<DoctorWithRating> findDoctorProfileById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findDoctorByUsername(String username);

    Long countDoctor(Map<String, String> params);

    PaginatedResult<User> doctorListWithoutRating(Map<String, String> params);

    Optional<User> findDoctorByIdWithLicense(Long id);
}
